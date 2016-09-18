package net.ncguy.argent.editor.tools.picker;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import net.ncguy.argent.Argent;
import net.ncguy.argent.editor.project.EditorScene;
import net.ncguy.argent.editor.project.ProjectManager;
import net.ncguy.argent.injector.ArgentInjector;
import net.ncguy.argent.injector.Inject;
import net.ncguy.argent.utils.AppUtils;

import java.nio.ByteBuffer;
import java.util.function.BiConsumer;

/**
 * Created by Guy on 30/07/2016.
 */
public abstract class BasePicker<T> implements Disposable {


    protected FrameBuffer fbo;

    protected BiConsumer<Integer, Integer> onResize;

    @Inject
    private ProjectManager projectManager;

    public BasePicker() {
        ArgentInjector.inject(this);
        refreshFBO(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.onResize = this::onResize;
        Argent.addOnResize(this.onResize);
    }

    private void onResize(int w, int h) {
        refreshFBO(w, h);
    }

    private void refreshFBO(int w, int h) {
        if(fbo != null) {
            fbo.dispose();
            fbo = null;
        }
        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, w, h, false);
    }

    public void begin(Viewport viewport) {
        fbo.begin();
        AppUtils.GL.clear();
//        HdpiUtils.glViewport(viewport.getScreenX(), viewport.getScreenY(), viewport.getScreenWidth(), viewport.getScreenHeight());
    }

    public Pixmap getFBOPixmap(Viewport viewport) {
//        int x = viewport.getScreenX();
//        int y = viewport.getScreenY();
//        int w = viewport.getScreenWidth();
//        int h = viewport.getScreenHeight();
        int x = 0;
        int y = 0;
        int w = fbo.getWidth();
        int h = fbo.getHeight();

        final ByteBuffer pixelBuffer = BufferUtils.newByteBuffer(w * h * 4);
        Gdx.gl.glBindFramebuffer(GL20.GL_FRAMEBUFFER, fbo.getFramebufferHandle());
        Gdx.gl.glReadPixels(x, y, w, h, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE, pixelBuffer);
        Gdx.gl.glBindFramebuffer(GL20.GL_FRAMEBUFFER, 0);

        final int numBytes = w * h * 4;
        byte[] imgLines = new byte[numBytes];
        final int numBytesPerLine = w * 4;
        for(int i = 0; i < h; i++) {
            pixelBuffer.position((h - i - 1) * numBytesPerLine);
            pixelBuffer.get(imgLines, i * numBytesPerLine, numBytesPerLine);
        }

        Pixmap map = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        BufferUtils.copy(imgLines, 0, map.getPixels(), imgLines.length);
        return map;
    }

    public void end() {
        fbo.end();
    }

    public abstract T pick(EditorScene scene, int screenX, int screenY);

    @Override
    public void dispose() {
        if(fbo != null) {
            fbo.dispose();
            fbo = null;
        }
        Argent.removeOnResize(this.onResize);
    }
}
