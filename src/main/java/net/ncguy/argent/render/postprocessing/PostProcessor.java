package net.ncguy.argent.render.postprocessing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;
import net.ncguy.argent.utils.MultiTargetFrameBuffer;

/**
 * Created by Guy on 31/07/2016.
 */
public abstract class PostProcessor implements Disposable {

    public final int ATTACHMENTS;
    protected int w, h;

    protected OrthographicCamera camera;
    protected MultiTargetFrameBuffer mrt;
    protected MultiTargetFrameBuffer mrt() {
        if(mrt == null) {
            mrt = MultiTargetFrameBuffer.create(Pixmap.Format.RGBA8888, ATTACHMENTS, w, h, true, false);
        }
        return mrt;
    }

    public PostProcessor(int attachments) {
        ATTACHMENTS = attachments;
        w = Gdx.graphics.getWidth();
        h = Gdx.graphics.getHeight();
        camera = new OrthographicCamera(w, h);
    }

    public void resize(float x, float y) {
        if(x == w && y == h) return;
        w = (int) x;
        h = (int) y;
        if(mrt != null) {
            mrt.dispose();
            mrt = null;
        }
        camera.setToOrtho(true, x, y);
    }

    public Texture getTexture() {
        return mrt().getColorBufferTexture();
    }
    public Texture getTexture(int index) {
        index = MathUtils.clamp(index, 0, ATTACHMENTS);
        return mrt().getColorBufferTexture(index);
    }

    protected abstract SpriteBatch spriteBatch();

    public void render(Texture texture) {
        spriteBatch().setProjectionMatrix(camera.combined);
        spriteBatch().begin();
        spriteBatch().draw(texture, 0, 0, w, h);
        spriteBatch().end();
    }

    @Override
    public void dispose() {
        spriteBatch().dispose();
        mrt().dispose();
    }
}
