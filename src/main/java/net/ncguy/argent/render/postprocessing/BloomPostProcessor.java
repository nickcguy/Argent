package net.ncguy.argent.render.postprocessing;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer;
import net.ncguy.argent.utils.MultiTargetFrameBuffer;

/**
 * Created by Guy on 31/07/2016.
 */
public class BloomPostProcessor extends BasicPostProcessor {

    int iterations = 5;

    private MultiTargetFrameBuffer fbo;

    public BloomPostProcessor() {
        super("post/bloom", 1);
    }

    protected MultiTargetFrameBuffer fbo() {
        if(fbo == null)
            fbo = MultiTargetFrameBuffer.create(Pixmap.Format.RGBA8888, ATTACHMENTS, w, h, true, false);
        return fbo;
    }

    private GLFrameBuffer<Texture> getActiveFBO(int index) {
        if(index % 2 == 0) return fbo();
        return mrt();
    }
    private GLFrameBuffer<Texture> getOtherFBO(int index) {
        if(index % 2 == 0) return mrt();
        return fbo();
    }

    @Override
    public void render(Texture texture) {
        int texId = 1;
        iterations = 5;
        spriteBatch().setProjectionMatrix(camera.combined);
        shaderProgram.begin();
        shaderProgram.setUniformi("u_image", texId);
        for(int i = 0; i < iterations; i++) {
            Texture tex;
            if(i == 0) tex = texture;
            else tex = getOtherFBO(i).getColorBufferTexture();
            shaderProgram.setUniformi("u_horizontal", i % 2);
            tex.bind(texId);
            shaderProgram.setUniformi("u_image", texId);
            getActiveFBO(i).begin();
            spriteBatch().begin();
            spriteBatch().draw(texture, 0, 0, w, h);
            spriteBatch().end();
            getActiveFBO(i).end();
        }
        shaderProgram.end();
    }

    @Override
    public Texture getTexture() {
        return getOtherFBO(iterations).getColorBufferTexture();
    }

    @Override
    public void dispose() {
        super.dispose();
        fbo.dispose();
    }

    @Override
    public void resize(float x, float y) {
        if(fbo != null) {
            fbo.dispose();
            fbo = null;
        }
        super.resize(x, y);
    }
}
