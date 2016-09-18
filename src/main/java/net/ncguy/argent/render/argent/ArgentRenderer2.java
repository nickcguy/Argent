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
import com.kotcrab.vis.ui.util.ColorUtils;
import net.ncguy.argent.Argent;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.entity.components.ArgentComponent;
import net.ncguy.argent.entity.components.light.LightComponent;
import net.ncguy.argent.render.BasicWorldRenderer;
import net.ncguy.argent.render.postprocessing.BloomPostProcessor;
import net.ncguy.argent.utils.AppUtils;
import net.ncguy.argent.utils.MultiTargetFrameBuffer;
import net.ncguy.argent.utils.ScreenshotUtils;
import net.ncguy.argent.utils.TextureCache;
import net.ncguy.argent.world.GameWorld;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guy on 23/07/2016.
 */
public class ArgentRenderer2<T extends WorldEntity> extends BasicWorldRenderer<T> {

    protected ModelBatch flatBatch;

    protected MultiTargetFrameBuffer mrtFbo;
    protected ShaderProgram shaderProgram;

    public boolean debug = false;

    public Runnable preDisplay, postDisplay;

    public void preDisplay() { if(preDisplay != null) preDisplay.run(); }
    public void postDisplay() { if(postDisplay != null) postDisplay.run(); }

    BloomPostProcessor bloomProcessor;

    public static final FBOAttachment POSITION = new FBOAttachment(0, "bufPosition");
    public static final FBOAttachment NORMAL = new FBOAttachment(1, "bufNormal");
    public static final FBOAttachment DIFFUSE = new FBOAttachment(2, "bufDiffuse");
    public static final FBOAttachment SPECULAR = new FBOAttachment(3, "bufSpecular");
    public static final FBOAttachment AMBIENT = new FBOAttachment(4, "bufAmbient");
    public static final FBOAttachment DISPLACEMENT = new FBOAttachment(5, "bufDisplacement");
    public static final FBOAttachment DEPTH = new FBOAttachment(6, "bufDepth");
    public static final FBOAttachment EMISSIVE = new FBOAttachment(7, "bufEmissive");

    public static final FBOAttachment[] attachments = new FBOAttachment[]{
            POSITION, NORMAL, DIFFUSE, SPECULAR, AMBIENT, DISPLACEMENT, DEPTH, EMISSIVE
    };

    public static final FBOAttachment POST_EMISSIVE = new FBOAttachment(-1, "postEmissive");

    public static final FBOAttachment[] postProcessorAttachments = new FBOAttachment[]{
            POST_EMISSIVE
    };

    public static final int COLOUR_TARGETS = attachments.length;

    public ArgentRenderer2(GameWorld<T> world) {
        super(world);
        this.bloomProcessor = new BloomPostProcessor();
        invalidateFlatBatch();
        invalidateMrtFbo();
        Argent.addOnResize(this::resize);
    }

    protected ShaderProgram finalProgram;
    protected ModelBatch finalBatch;

    public ModelBatch finalBatch() {
        if(finalBatch == null) {
            ShaderProgram.pedantic = false;
            finalProgram = AppUtils.Shader.loadShader("lighting");
            finalBatch = new ModelBatch(new DefaultShaderProvider() {
                @Override
                protected Shader createShader(Renderable renderable) {
                    return new ArgentLightShader(renderable, finalProgram, null);
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

    public void invalidateFlatBatch() {
        if(this.flatBatch != null) {
            this.flatBatch.dispose();
            this.flatBatch = null;
        }
        this.flatBatch = new ModelBatch(new DefaultShaderProvider(){
            @Override
            protected Shader createShader(Renderable renderable) {
                return new FlatShader(renderable);
            }
        });
    }

    public void renderOnClean(Shader shader) {
        this.flatBatch.begin(camera());
        this.flatBatch.render(world.instances(), shader);
        this.flatBatch.end();
    }

    @Override
    public void resize(int width, int height) {
        if(camera().viewportWidth == width && camera().viewportHeight == height) return;
        super.resize(width, height);
        camera().viewportWidth = width;
        camera().viewportHeight = height;
        camera().update(true);

        invalidateMrtFbo();
//        invalidateFlatBatch();

        ortho = null;
        if(debugFbo != null) debugFbo.dispose();
        debugFbo = null;

        this.bloomProcessor.resize(width, height);
    }

    @Override
    public void render(ModelBatch batch, float delta) {
//        super.render(super.batch(), delta);
        renderGBuffer(batch, delta);
        applyPostProcessing();
        preDisplay();
        renderDebug();
        renderLighting(delta);
        postDisplay();
        this.flatBatch.begin(camera());
        separateRenderers.forEach(r -> {
            r.render(this.flatBatch, delta);
        });
        this.flatBatch.end();
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

    public void applyPostProcessing() {
        this.bloomProcessor.render(this.mrtFbo().getColorBufferTexture(EMISSIVE.id));
//        this.mrtFbo().overrideBufferContents(EMISSIVE.id, this.bloomProcessor.getTexture());
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
        final int[] i = {0};
        lights.forEach(l -> {
            String key = "lights["+ i[0] +"]";
            float[] pos = new float[3];
            Vector3 worldPos = l.getWorldPosition();
            pos[0] = worldPos.x;
            pos[1] = worldPos.y;
            pos[2] = worldPos.z;
            this.finalProgram.setUniform3fv(key+".Position", pos, 0, pos.length);
            float[] col = new float[3];
            Color colour = l.getDiffuse();
            col[0] = colour.r;
            col[1] = colour.g;
            col[2] = colour.b;
            this.finalProgram.setUniform3fv(key+".Colour", col, 0, col.length);
            i[0]++;
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
        if(!debug) return;
        Sprite[] sprites = new Sprite[COLOUR_TARGETS];
        for(int i = 0; i < sprites.length; i++) {
            sprites[i] = new Sprite(this.mrtFbo.getColorBufferTexture(i));
        }
        debugBatch().setProjectionMatrix(ortho().combined);
        debugBatch().begin();
        float w, h;

//        w = Gdx.graphics.getWidth()/2;
//        h = Gdx.graphics.getHeight()/2;
        w = camera().viewportWidth;
        h = camera().viewportHeight;

//        w = camera().viewportWidth / 2;
//        h = camera().viewportHeight / 2;
        float ratio = w / h;
        h = camera().viewportHeight / COLOUR_TARGETS;
        w = h * ratio;

        float wOffset = COLOUR_TARGETS % 2 == 0 ? w : w/2;
        float hOffset = COLOUR_TARGETS % 2 == 0 ? 0 : h/2;
        float x = -camera().viewportWidth + (w * (COLOUR_TARGETS/2) - wOffset);
        float y = -camera().viewportHeight + (h * (COLOUR_TARGETS/2) + hOffset);

        x -= 3;
        y -= 13;
//        sprites[POSITION.id].setBounds(-w+1,  0+1, w-2, h-2);
//        sprites[NORMAL  .id].setBounds(-w+1, -h+1, w-2, h-2);
//        sprites[DIFFUSE .id].setBounds(0+1, 0+1, w-2, h-2);
//        sprites[SPECULAR.id].setBounds(0+1, -h+1, w-2, h-2);

//
//        debugBatch().setColor(Color.RED);
//        debugBatch().draw(TextureCache.white(), -w,  0, w, h);
//        debugBatch().setColor(Color.GREEN);
//        debugBatch().draw(TextureCache.white(), -w, -h, w, h);
//        debugBatch().setColor(Color.BLUE);
//        debugBatch().draw(TextureCache.white(),  0,  0, w, h);
//        debugBatch().setColor(Color.PURPLE);
//        debugBatch().draw(TextureCache.white(),  0, -h, w, h);
//        debugBatch().setColor(Color.WHITE);
        int index = COLOUR_TARGETS;
        for(Sprite s : sprites) {
            index--;
            float fX = x;
            fX += (w);
            float fY = y;
            fY += (h)*index;
            s.setBounds(fX+1, fY+1, w-2, h-2);
            debugBatch().setColor(ColorUtils.HSVtoRGB((360/COLOUR_TARGETS)*index, 75, 50));
            debugBatch().draw(TextureCache.white(), fX,  fY, w, h);
            debugBatch().setColor(Color.WHITE);
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
        invalidateFlatBatch();
    }
    @Override
    public ModelBatch batch() {
        if(modelBatch == null) {
            shaderProgram = AppUtils.Shader.loadShader("gbuffer");
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
        int id = index + shaderOffset;
        this.bloomProcessor.getTexture().bind(id);
        this.finalProgram.setUniformi(POST_EMISSIVE.name, id);
        index++;
//        for (FBOAttachment attachment : postProcessorAttachments) {
//            int id = index + shaderOffset;
//            mrt.getColorBufferTexture(index).bind(id);
//            this.finalProgram.setUniformi(attachment.name, id);
//            index++;
//        }

        this.finalProgram.end();
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

