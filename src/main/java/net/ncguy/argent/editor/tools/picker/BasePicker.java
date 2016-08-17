package net.ncguy.argent.editor.tools.picker;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.Disposable;
import net.ncguy.argent.editor.project.EditorScene;
import net.ncguy.argent.editor.project.ProjectManager;
import net.ncguy.argent.injector.ArgentInjector;
import net.ncguy.argent.injector.Inject;

/**
 * Created by Guy on 30/07/2016.
 */
public abstract class BasePicker<T> implements Disposable {


    @Inject
    private ProjectManager projectManager;

    public BasePicker() {
        ArgentInjector.inject(this);
    }

    public Pixmap getFBOPixmap() {

        return new Pixmap(1, 1, Pixmap.Format.RGBA8888);

//        AbstractWorldRenderer tmp = projectManager.current().currScene.sceneGraph.renderer;
//        ArgentRenderer renderer;
//        if(tmp instanceof ArgentRenderer) {
//            renderer = (ArgentRenderer)tmp;
//        }else return null;
//
//        MultiTargetFrameBuffer mrt = renderer.mrtFbo();
//
//        int x = 0;
//        int y = 0;
//        int w = mrt.getWidth();
//        int h = mrt.getHeight();
//
//        final int numBytes = w * h * 4;
//        final ByteBuffer pixelBuffer = BufferUtils.newByteBuffer(numBytes);
//
//        Gdx.gl.glBindFramebuffer(GL_FRAMEBUFFER, mrt.getFramebufferHandle());
//        int index = 0;
//        gl30.glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0 + index, GL_TEXTURE_2D, mrt.getColorBufferTexture(index).getTextureObjectHandle(), 0);
//        Gdx.gl.glReadPixels(x, y, w, h, GL_RGBA, GL_UNSIGNED_BYTE, pixelBuffer);
//        Gdx.gl.glBindFramebuffer(GL_FRAMEBUFFER, 0);
//
//        byte[] imgLines = new byte[numBytes];
//        final int numBytesPerLine = w * 4;
//        for(int i = 0; i < h; i++) {
//            pixelBuffer.position((h - i - 1) * numBytesPerLine);
//            pixelBuffer.get(imgLines, i * numBytesPerLine, numBytesPerLine);
//        }
//
//        Pixmap map = new Pixmap(w, h, Pixmap.Format.RGBA8888);
//        BufferUtils.copy(imgLines, 0, map.getPixels(), imgLines.length);
//        return map;
    }

    public abstract T pick(EditorScene scene, int screenX, int screenY);

    @Override
    public void dispose() {

    }
}
