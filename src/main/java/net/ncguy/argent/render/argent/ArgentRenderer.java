package net.ncguy.argent.render.argent;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import net.ncguy.argent.Argent;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.entity.components.ArgentComponent;
import net.ncguy.argent.entity.components.LightComponent;
import net.ncguy.argent.render.BasicWorldRenderer;
import net.ncguy.argent.utils.MultiTargetFrameBuffer;
import net.ncguy.argent.utils.ScreenshotUtils;
import net.ncguy.argent.utils.TextureCache;
import net.ncguy.argent.world.GameWorld;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guy on 23/07/2016.
 */
public class ArgentRenderer<T extends WorldEntity> extends BasicWorldRenderer<T> {

    protected MultiTargetFrameBuffer mrtFbo;
    protected ShaderProgram shaderProgram;

    public static final FBOAttachment POSITION = new FBOAttachment(0, "bufPosition");
    public static final FBOAttachment NORMAL = new FBOAttachment(1, "bufNormal");
    public static final FBOAttachment DIFFUSE = new FBOAttachment(2, "bufDiffuse");
    public static final FBOAttachment SPECULAR = new FBOAttachment(3, "bufSpecular");

    public static final FBOAttachment[] attachments = new FBOAttachment[]{
            POSITION, NORMAL, DIFFUSE, SPECULAR
    };

    public static final int COLOUR_TARGETS = 4;

    public ArgentRenderer(GameWorld<T> world) {
        super(world);
        invalidateMrtFbo();
        Argent.addOnResize(this::resize);
    }

    protected ShaderProgram finalProgram;
    protected ModelBatch finalBatch;

    public ModelBatch finalBatch() {
        if(finalBatch == null) {
            ShaderProgram.pedantic = false;
            finalProgram = loadShader("lighting");
            finalBatch = new ModelBatch(new DefaultShaderProvider() {
                @Override
                protected Shader createShader(Renderable renderable) {
                    return new ArgentShader(renderable, finalProgram);
                }
            });
        }
        return finalBatch;
    }

    public MultiTargetFrameBuffer mrtFbo() {
        if(this.mrtFbo == null) invalidateMrtFbo();
        return this.mrtFbo;
    }

    public void invalidateMrtFbo() {
        if(this.mrtFbo != null) {
            this.mrtFbo.dispose();
            this.mrtFbo = null;
        }
        this.mrtFbo = MultiTargetFrameBuffer.create(Pixmap.Format.RGBA8888, COLOUR_TARGETS, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true, false);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        camera().viewportWidth = width;
        camera().viewportHeight = height;
        camera().update(true);

        invalidateMrtFbo();
        ortho = null;
        if(debugFbo != null) debugFbo.dispose();
        debugFbo = null;
    }

    @Override
    public void render(ModelBatch batch, float delta) {
//        super.render(super.batch(), delta);
        renderGBuffer(batch, delta);
        renderDebug();
        renderLighting(delta);
        if(Gdx.input.isKeyJustPressed(Input.Keys.O))
            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
                reloadShader();
    }

    public void renderGBuffer(ModelBatch batch, float delta) {
        this.mrtFbo.begin();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
        super.render(batch, delta);
        this.mrtFbo.end();
    }

    public void renderLighting(float delta) {
        bindMRT(this.mrtFbo);
        bindLightData();
        super.render(finalBatch(), delta);
    }

    public void bindLightData() {
        final List<LightComponent> lights = new ArrayList<>();
        world.instances().forEach(i -> i.components().stream()
                .filter(this::bindLightData_Filter)
                .map(this::bindLightData_Map)
                .forEach(lights::add));
        this.finalProgram.begin();
        int i = 0;
        lights.forEach(l -> {
            String key = "lights["+i+"]";
            float[] pos = new float[3];
            Vector3 worldPos = l.getWorldPosition();
            pos[0] = worldPos.x;
            pos[1] = worldPos.y;
            pos[2] = worldPos.z;
            this.finalProgram.setUniform3fv(key+".Position", pos, 0, pos.length);
            float[] col = new float[3];
            Color colour = l.getColour();
            col[0] = colour.r;
            col[1] = colour.g;
            col[2] = colour.b;
            this.finalProgram.setUniform3fv(key+".Colour", col, 0, col.length);
            this.finalProgram.setUniformf(key+".Linear", l.getLinear());
            this.finalProgram.setUniformf(key+".Quadratic", l.getQuadratic());
            this.finalProgram.setUniformf(key+".Intensity", l.getIntensity());
        });
        this.finalProgram.end();
    }
    private boolean bindLightData_Filter(ArgentComponent c) {
        return c instanceof LightComponent;
    }
    private LightComponent bindLightData_Map(ArgentComponent c) {
        return (LightComponent)c;
    }

    protected SpriteBatch debugBatch;
    protected SpriteBatch debugBatch() {
        if(debugBatch == null) {
            debugBatch = new SpriteBatch();
        }
        return debugBatch;
    }
    protected OrthographicCamera ortho;
    protected OrthographicCamera ortho() {
        if(ortho == null) {
            ortho = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
        return ortho;
    }
    protected FrameBuffer debugFbo;
    protected FrameBuffer debugFbo() {
        if(debugFbo == null) {
            debugFbo = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        }

        return debugFbo;
    }
    public void renderDebug() {
//        Gdx.gl.glClearColor(1, 1, 1, 1);
//        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
        Sprite[] sprites = new Sprite[COLOUR_TARGETS];
        for(int i = 0; i < sprites.length; i++) {
            sprites[i] = new Sprite(this.mrtFbo.getColorBufferTexture(i));
        }
        debugBatch().setProjectionMatrix(ortho().combined);
        debugBatch().begin();
        float w, h;

//        w = Gdx.graphics.getWidth()/2;
//        h = Gdx.graphics.getHeight()/2;
        w = camera().viewportWidth / 2;
        h = camera().viewportHeight / 2;
        sprites[POSITION.id].setBounds(-w+1,  0+1, w-2, h-2);
        sprites[NORMAL  .id].setBounds(-w+1, -h+1, w-2, h-2);
        sprites[DIFFUSE .id].setBounds(0+1, 0+1, w-2, h-2);
        sprites[SPECULAR.id].setBounds(0+1, -h+1, w-2, h-2);


        debugBatch().setColor(Color.RED);
        debugBatch().draw(TextureCache.pixel(), -w,  0, w, h);
        debugBatch().setColor(Color.GREEN);
        debugBatch().draw(TextureCache.pixel(), -w, -h, w, h);
        debugBatch().setColor(Color.BLUE);
        debugBatch().draw(TextureCache.pixel(),  0,  0, w, h);
        debugBatch().setColor(Color.PURPLE);
        debugBatch().draw(TextureCache.pixel(),  0, -h, w, h);
        debugBatch().setColor(Color.WHITE);

        for(Sprite s : sprites) {
            s.setFlip(false, true);
            s.draw(debugBatch());
        }

//        freeCamController.target.set(0, 0, 0);
//        camera().lookAt(freeCamController.target);

        debugBatch().end();
        if(Gdx.input.isKeyJustPressed(Input.Keys.F2))
            ScreenshotUtils.saveScreenshot(mrtFbo().getWidth(), mrtFbo().getHeight(), "Debug");
    }

    @Override
    public Environment environment() {
        return null;
    }


    public void reloadShader() {
        if(modelBatch != null) {
            modelBatch.dispose();
            modelBatch = null;
        }
        if(shaderProgram != null) {
            shaderProgram.dispose();
            shaderProgram = null;
        }
        if(finalBatch != null) {
            finalBatch.dispose();
            finalBatch = null;
        }
        if(finalProgram != null) {
            finalProgram.dispose();
            finalProgram = null;
        }
    }
    @Override
    public ModelBatch batch() {
        if(modelBatch == null) {
            shaderProgram = loadShader("gbuffer");
            modelBatch = new ModelBatch(new DefaultShaderProvider() {
                @Override
                protected Shader createShader(Renderable renderable) {
                    return new SmartTextureShader(renderable, shaderProgram);
                }
            });
        }
        return modelBatch;
    }

    public static final int shaderOffset = 0;
    public void bindMRT(MultiTargetFrameBuffer mrt) {
        if(this.finalProgram == null) finalBatch();
        int index = 0;
        this.finalProgram.begin();
        for (FBOAttachment attachment : attachments) {
            int id = index + shaderOffset;
            mrt.getColorBufferTexture(index).bind(id);
            this.finalProgram.setUniformi(attachment.name, id);
            index++;
        }
        this.finalProgram.end();
    }

    public static final String shaderFormat = "assets/shaders/%s.%s";
    public ShaderProgram loadShader(String prefix) {
        String vertPath = String.format(shaderFormat, prefix, "vert");
        String fragPath = String.format(shaderFormat, prefix, "frag");
        ShaderProgram.pedantic = false;
        ShaderProgram prg = new ShaderProgram(Gdx.files.internal(vertPath), Gdx.files.internal(fragPath));
        System.out.println(prg.getLog());
        if(!prg.isCompiled()) return null;
        return prg;
    }

    public static class FBOAttachment {
        public int id;
        public String name;

        public FBOAttachment(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }

}
