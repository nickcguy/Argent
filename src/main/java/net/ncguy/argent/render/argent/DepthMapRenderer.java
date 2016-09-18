package net.ncguy.argent.render.argent;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.FrameBufferCubemap;
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.entity.components.light.LightComponent;
import net.ncguy.argent.render.BasicWorldRenderer;
import net.ncguy.argent.utils.AppUtils;
import net.ncguy.argent.utils.ScreenshotUtils;
import net.ncguy.argent.world.GameWorld;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Guy on 16/09/2016.
 */
public class DepthMapRenderer<T extends WorldEntity> extends BasicWorldRenderer<T> {

    static int SHADOW_SIZE = 1024;
    FrameBuffer depthFBO;
    FrameBufferCubemap depthCube;
    ShaderProgram depthProgram;
    OrthographicCamera ortho;

    public DepthMapRenderer(GameWorld<T> world) {
        super(world);
    }

    public FrameBufferCubemap depthCube() {
        if (depthCube == null) {
            depthCube = new FrameBufferCubemap(Pixmap.Format.RGBA8888, SHADOW_SIZE, SHADOW_SIZE, true);
        }
        return depthCube;
    }

    public FrameBuffer depthFBO() {
        if(depthFBO == null) depthFBO = new FrameBuffer(Pixmap.Format.RGBA8888, SHADOW_SIZE, SHADOW_SIZE, true);
        return depthFBO;
    }

    public OrthographicCamera getOrtho() {
        if(ortho == null) ortho = new OrthographicCamera(SHADOW_SIZE, SHADOW_SIZE);
        return ortho;
    }

    public PerspectiveCamera getPersp() {
        return camera();
    }

    @Override
    public PerspectiveCamera camera() {
        return super.camera();
    }

    public void invalidateBatch() {
        if(depthProgram != null) {
            depthProgram.dispose();
            depthProgram = null;
        }
        if(modelBatch != null) {
            modelBatch.dispose();
            modelBatch = null;
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        invalidateBatch();

        if(depthFBO != null) {
            depthFBO.dispose();
            depthFBO = null;
        }

        if(depthCube != null) {
            depthCube.dispose();
            depthCube = null;
        }
    }

    @Override
    public ModelBatch batch() {
        if(modelBatch == null) {
            depthProgram = AppUtils.Shader.loadShader("pipeline/util/depth");
            if(depthProgram.isCompiled())
                modelBatch = new ModelBatch(new DefaultShaderProvider() {
                    @Override
                    protected Shader createShader(Renderable renderable) {
                        return new DepthMapShader(renderable, depthProgram);
                    }
                });
            else modelBatch = new ModelBatch();
        }
        return modelBatch;
    }

    @Override
    public void render(ModelBatch batch, float delta) {
        List<LightComponent> lights = world().instances()
                .stream()
                .filter(e -> e.has(LightComponent.class))
                .map(e -> e.get(LightComponent.class))
                .collect(Collectors.toList());

        renderFromLights(lights, delta);
    }

    public void renderFromLights(List<LightComponent> lights, float delta) {
        lights.stream()
                .filter(LightComponent::isDirtyShadows)
                .forEach(l -> {
                    l.bindShadow(this);
                    renderToDepthBuffer(delta, l);
                });
    }

    @Override
    public Environment environment() {
        return null;
    }

    private void renderToDepthBuffer(float delta, LightComponent light) {
        GLFrameBuffer fbo = light.selectFBO(this);

        iterate(fbo, () -> {
            AppUtils.GL.clear();
            batch().begin(light.selectCamera(this));
            batch().render(world.instances());
            batch().end();
        });
    }

    private void iterate(GLFrameBuffer fbo, Runnable iter) {
        fbo.begin();
        if(fbo instanceof FrameBufferCubemap) {
            while (((FrameBufferCubemap) fbo).nextSide()) {
                iter.run();
                if(Gdx.input.isKeyJustPressed(Input.Keys.F2))
                    ScreenshotUtils.saveScreenshot(SHADOW_SIZE, SHADOW_SIZE, "DepthBuffer_Cubemap");
            }
        }else{
            iter.run();
            if(Gdx.input.isKeyJustPressed(Input.Keys.F2))
                ScreenshotUtils.saveScreenshot(SHADOW_SIZE, SHADOW_SIZE, "DepthBuffer");
        }
        fbo.end();
    }

}
