package net.ncguy.argent.render.custom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;
import net.ncguy.argent.Argent;

/**
 * Created by Guy on 15/07/2016.
 */
public abstract class BufferRenderer<T extends RenderableProvider> implements Disposable {

    protected Color clearColour;

    protected CustomWorldRenderer<T> renderer;
    protected FrameBuffer fbo;
    protected ShaderProgram shaderProgram;
    protected ModelBatch modelBatch;

    public BufferRenderer(CustomWorldRenderer<T> renderer) {
        this(renderer, Color.BLACK);
    }

    public BufferRenderer(CustomWorldRenderer<T> renderer, Color clearColour) {
        this.renderer = renderer;
        this.clearColour = clearColour;
        Argent.addOnResize(this::resize);
    }

    protected void resize(int w, int h) {
        if(this.fbo != null) this.fbo.dispose();
        this.fbo = new FrameBuffer(Pixmap.Format.RGBA8888, w, h, true);
    }

    protected FrameBuffer fbo() {
        if(fbo == null) initFBO();
        return fbo;
    }

    protected void initFBO() {
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    /**
     * Initialises the shaderprogram and attaches it to the modelbatch
     */
    protected void initShaderBatch() {
        modelBatch = new ModelBatch(new DefaultShaderProvider(){
            @Override
            protected Shader createShader(Renderable renderable) {
                return new DefaultShader(renderable);
            }
        });
    }

    public void attachBufferToShader(final ShaderProgram shader, final int[] mutableId) {
        final int texNum = mutableId[0]++;
        getBufferContents().bind(texNum);
        shader.setUniformi(u_name(), texNum);
    }

    public Texture getBufferContents() {
        return fbo().getColorBufferTexture();
    }

    public abstract String name();
    public String u_name() {
        return "u_"+name();
    }


    /**
     * Prepares the instance for rendering, ideal place to set shader uniforms
     * @return this instance for method chaining
     */
    public BufferRenderer<T> prepare() {
        return this;
    }

    public BufferRenderer<T> render() {
        return render(true);
    }

    public BufferRenderer<T> render(boolean intoFbo) {
        if(intoFbo)
            return renderIntoFBO();
        return renderIntoBatch();
    }

    public BufferRenderer<T> renderIntoFBO() {
        fbo().begin();
        Gdx.gl.glClearColor(clearColour.r, clearColour.g, clearColour.b, clearColour.a);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
        renderIntoBatch();
        fbo().end();
        return this;
    }

    public BufferRenderer<T> renderIntoBatch() {
        modelBatch.begin(renderer.camera());
        modelBatch.render(renderer.world().instances());
        modelBatch.end();
        return this;
    }

    @Override
    public void dispose() {
        if(this.fbo != null) {
            this.fbo.dispose();
            this.fbo = null;
        }
    }
}
