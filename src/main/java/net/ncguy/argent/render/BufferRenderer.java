package net.ncguy.argent.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import net.ncguy.argent.utils.TextUtils;

/**
 * Created by Guy on 13/06/2016.
 */
public abstract class BufferRenderer<T> {

    protected Color screenClearColour, fboClearColour;

    protected WorldRenderer<T> renderer;
    protected FrameBuffer fbo;
    protected ShaderProgram shaderProgram;
    protected ModelBatch modelBatch;

    public BufferRenderer(WorldRenderer<T> renderer) {
        this(renderer, true);
    }
    public BufferRenderer(WorldRenderer<T> renderer, boolean doInit) {
        this.renderer = renderer;
        this.screenClearColour = new Color(.3f, .3f, .3f, 1);
        this.fboClearColour = new Color(0, 0, 0, 1);
        if(doInit)
            init();
    }

    protected FrameBuffer fbo() {
        if (fbo == null) initFrameBuffer();
        return fbo;
    }

    protected void initFrameBuffer() {
        Vector2 s = size();
        this.fbo = new FrameBuffer(Pixmap.Format.RGBA8888, (int)s.x, (int)s.y, true);
    }


    /**
     * Initialize the shaderProgram and attach it to the modelBatch
     * If {link@canBeReloaded} is true, insure that the shader binding has guards to prevent invalid compilation
     */
    public abstract void init();
    public abstract void setSceneUniforms(ShaderProgram program, int[] mutableId);

    public void render(float delta) {
        render(delta, renderer.finalBuffer == this);
    }

    public void render(float delta, boolean toScreen) {
        if(toScreen) renderToScreen(delta);
        else renderToFBO(delta);
    }

    public void renderToScreen(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        renderIntoBatch();
    }

    public void renderToFBO(float delta) {
        fbo().begin();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        renderIntoBatch();
        fbo().end();
    }

    public void renderIntoBatch() {
        modelBatch.begin(renderer.camera());
        renderer.renderables().forEach(modelBatch::render);
        modelBatch.end();
    }

    public void invalidateFBO() {
        if(fbo == null) return;
        fbo.dispose();
        fbo = null;
    }

    public String name() {
        return "Unnamed buffer";
    }
    public boolean canBeReloaded() {
        return true;
    }

    public Vector2 size() { return new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); }

    public String uniformName() {
        return "u_"+ TextUtils.camelCase(name()).replace(" ", "");
    }

    public final Texture getBufferContents() {
        return fbo().getColorBufferTexture();
    }

}
