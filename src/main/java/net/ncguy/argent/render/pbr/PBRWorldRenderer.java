package net.ncguy.argent.render.pbr;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import net.ncguy.argent.Argent;
import net.ncguy.argent.assets.ArgentShaderProvider;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.entity.components.ArgentComponent;
import net.ncguy.argent.entity.components.light.DirLightComponent;
import net.ncguy.argent.entity.components.light.LightComponent;
import net.ncguy.argent.entity.components.light.PointLightComponent;
import net.ncguy.argent.entity.components.light.SpotLightComponent;
import net.ncguy.argent.event.shader.ReloadShaderEvent;
import net.ncguy.argent.render.AbstractWorldRenderer;
import net.ncguy.argent.render.argent.ArgentRenderer;
import net.ncguy.argent.render.pbr.shaders.PBR_SurfaceShader;
import net.ncguy.argent.utils.AppUtils;
import net.ncguy.argent.utils.MultiTargetFrameBuffer;
import net.ncguy.argent.utils.ScreenshotUtils;
import net.ncguy.argent.world.GameWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Guy on 23/12/2016.
 */
public class PBRWorldRenderer<T extends WorldEntity> extends AbstractWorldRenderer<T> implements ReloadShaderEvent.ReloadShaderListener {

    MultiTargetFrameBuffer mrtFbo;
    ModelBatch mutableBatch;

    ShaderProgram shader;
    int currentSize;

    public PBRWorldRenderer(GameWorld<T> world) {
        super(world);
        Argent.event.register(this);
        loadShaders();
        mutableBatch = new ModelBatch(new ArgentShaderProvider());
    }

    public void loadShaders() {
        if(shader != null) {
            shader.dispose();
            shader = null;
        }

        String vert = Gdx.files.internal("assets/shaders/pbr/surface.vert").readString();
        String frag = Gdx.files.internal("assets/shaders/pbr/surface_tex.frag").readString();

        List<PointLightComponent> pointLights = fetchLightingData(PointLightComponent.class);

        int size = pointLights.size();
        if(size < 1) size = 1;

        currentSize = size;

        frag = frag.replace("LIGHT_COUNT", size+"");

        shader = AppUtils.Shader.compileShader(vert, frag);
    }

    public void closeFbo() {
        if(mrtFbo != null) {
            mrtFbo.dispose();
            mrtFbo = null;
        }
    }

    public MultiTargetFrameBuffer fbo() {
        if(mrtFbo == null)
            mrtFbo = MultiTargetFrameBuffer.create(Pixmap.Format.RGBA8888, 6, Math.max(Gdx.graphics.getWidth(), 1), Math.max(Gdx.graphics.getHeight(), 1), true, false);
        return mrtFbo;
    }

    @Override
    public ModelBatch batch() {
        if(modelBatch == null) modelBatch = new ModelBatch(new DefaultShaderProvider() {
            @Override
            protected Shader createShader(Renderable renderable) {
                return new PBR_SurfaceShader(renderable, shader);
            }
        });
        return modelBatch;
    }

    @Override
    public void render(ModelBatch batch, float delta) {
        renderToMRT(fbo(), mutableBatch, delta);
        shader.begin();
        bindLightingData(shader, fetchLightingData(PointLightComponent.class));
        bindMRT(fbo(), shader, 0, ArgentRenderer.tex_ATTACHMENTS);
        shader.end();
        batch.begin(camera());
        batch.render(world.instances());
        batch.end();
    }

    private void renderToMRT(MultiTargetFrameBuffer mrt, ModelBatch batch, float delta) {
        mrt.begin();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
        batch.begin(camera());
        batch.render(world.instances());
        batch.end();
        mrt.end();
    }

    private void bindMRT(MultiTargetFrameBuffer mrt, ShaderProgram shader, int offset, ArgentRenderer.FBOAttachment... attachments) {
        for (ArgentRenderer.FBOAttachment attachment : attachments) {
            int id = attachment.id + offset;
            Texture tex = mrt.getColorBufferTexture(attachment.id);
            if(tex != null) tex.bind(id);
            attachment.bind(shader, id);
        }
//        mrt.getColorBufferTexture(tex_DIFFUSE.id).bind(tex_DIFFUSE.id);
//        shader.setUniformi(tex_DIFFUSE.name, tex_DIFFUSE.id);
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
    }

    @Override
    public void dispose() {
        world.dispose();
    }

    @Override
    public void onReloadShader(ReloadShaderEvent event) {
        Gdx.app.postRunnable(() -> {
            this.loadShaders();
            if(modelBatch != null) {
                modelBatch.dispose();
                modelBatch = null;
            }
        });
    }

    public List<LightComponent>[] fetchLightingData() {
        final List<LightComponent> lights = new ArrayList<>();
        world.instances().forEach(i -> i.components().stream()
                .filter(this::bindLightData_Filter)
                .map(this::bindLightData_Map)
                .forEach(lights::add));
        List<LightComponent> dirLights = lights.stream().filter(l -> l instanceof DirLightComponent).collect(Collectors.toList());
        List<LightComponent> pointLights = lights.stream().filter(l -> l instanceof PointLightComponent).collect(Collectors.toList());
        List<LightComponent> spotLights = lights.stream().filter(l -> l instanceof SpotLightComponent).collect(Collectors.toList());

        return new List[]{dirLights, pointLights, spotLights};
    }

    public <T extends LightComponent> List<T> fetchLightingData(Class<T> type) {
        final List<T> lights = new ArrayList<>();
        world.instances().forEach(i -> i.components().stream()
                .filter(this::bindLightData_Filter)
                .map(this::bindLightData_Map)
                .filter(l -> l.getClass().isAssignableFrom(type))
                .map(l -> (T) l)
                .forEach(l -> lights.add(l)));
        return lights;
    }

    private boolean bindLightData_Filter(ArgentComponent c) {
        return c instanceof LightComponent;
    }
    private LightComponent bindLightData_Map(ArgentComponent c) {
        return (LightComponent)c;
    }

    public void bindLightingData(ShaderProgram shaderProgram, List<? extends LightComponent> pointLights) {
        final int[] index = {0};
        pointLights.forEach(l -> {
            int i = index[0]++;
            Vector3 position = l.getWorldPosition();
            Color colour = l.getDiffuse();
            float[] pos = new float[]{
                    position.x,
                    position.y,
                    position.z
            };
            float[] col = new float[]{
                    colour.r,
                    colour.g,
                    colour.b
            };
            shaderProgram.setUniform3fv("lights["+i+"].Position", pos, 0, pos.length);
            shaderProgram.setUniform3fv("lights["+i+"].Colour",   col, 0, col.length);
        });
        if(pointLights.size() != currentSize)
            loadShaders();
    }


}
