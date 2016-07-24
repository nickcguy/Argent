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
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.render.BasicWorldRenderer;
import net.ncguy.argent.utils.MultiTargetFrameBuffer;
import net.ncguy.argent.utils.ScreenshotUtils;
import net.ncguy.argent.utils.TextureCache;
import net.ncguy.argent.world.GameWorld;

/**
 * Created by Guy on 23/07/2016.
 */
public class ArgentRenderer<T extends WorldEntity> extends BasicWorldRenderer<T> {

    protected MultiTargetFrameBuffer mrtFbo;
    protected ShaderProgram shaderProgram;

    public static final int POSITION = 0;
    public static final int NORMAL   = 1;
    public static final int DIFFUSE  = 2;
    public static final int SPECULAR = 3;

    public static final int COLOUR_TARGETS = 4;

    public ArgentRenderer(GameWorld<T> world) {
        super(world);
        invalidateMrtFbo();
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
        invalidateMrtFbo();
    }

    @Override
    public void render(ModelBatch batch, float delta) {
        super.render(super.batch(), delta);
        renderGBuffer(batch, delta);
        renderDebug();
    }

    public void renderGBuffer(ModelBatch batch, float delta) {
        this.mrtFbo.begin();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
        super.render(batch, delta);
        this.mrtFbo.end();
    }

    public void renderLighting(float delta) {

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
        debugFbo().begin();
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
        Sprite[] sprites = new Sprite[COLOUR_TARGETS];
        for(int i = 0; i < sprites.length; i++)
            sprites[i] = new Sprite(this.mrtFbo.getColorBufferTexture(i));
        debugBatch().setProjectionMatrix(ortho().combined);
        debugBatch().begin();
        float w, h;
        w = Gdx.graphics.getWidth()/2;
        h = Gdx.graphics.getHeight()/2;
        sprites[POSITION].setBounds(-w+1,  0+1, w-2, h-2);
        sprites[NORMAL  ].setBounds(-w+1, -h+1, w-2, h-2);
        sprites[DIFFUSE ].setBounds(0+1, 0+1, w-2, h-2);
        sprites[SPECULAR].setBounds(0+1, -h+1, w-2, h-2);


        debugBatch().setColor(Color.RED);
        debugBatch().draw(TextureCache.pixel(), -w,  0, w, h);
        debugBatch().setColor(Color.GREEN);
        debugBatch().draw(TextureCache.pixel(), -w, -h, w, h);
        debugBatch().setColor(Color.BLUE);
        debugBatch().draw(TextureCache.pixel(),  0,  0, w, h);
        debugBatch().setColor(Color.PURPLE);
        debugBatch().draw(TextureCache.pixel(),  0, -h, w, h);
        debugBatch().setColor(Color.WHITE);

        for(Sprite s : sprites)
            s.draw(debugBatch());

        debugBatch().end();
        if(Gdx.input.isKeyJustPressed(Input.Keys.F2))
            ScreenshotUtils.saveScreenshot(mrtFbo().getWidth(), mrtFbo().getHeight(), "Debug");
        debugFbo().end();
    }

    @Override
    public Environment environment() {
        return null;
    }

    @Override
    public ModelBatch batch() {
        if(modelBatch == null) {
            shaderProgram = loadShader("gbuffer");
            modelBatch = new ModelBatch(new DefaultShaderProvider() {
                @Override
                protected Shader createShader(Renderable renderable) {
                    return new SimpleTextureShader(renderable, shaderProgram);
                }
            });
        }
        return modelBatch;
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

}
