package net.ncguy.argent.render;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer;
import com.badlogic.gdx.graphics.glutils.GLOnlyTextureData;

import java.util.List;

/**
 * Created by Guy on 14/06/2016.
 */
public class CompositeFrameBuffer extends GLFrameBuffer<Texture> {

    protected List<Texture> colorTextures;

    /** Creates a new FrameBuffer having the given dimensions and potentially a depth buffer attached. */
    public CompositeFrameBuffer (Pixmap.Format format, int width, int height, boolean hasDepth) {
        this(format, width, height, hasDepth, false);
    }

    /** Creates a new FrameBuffer having the given dimensions and potentially a depth and a stencil buffer attached.
     *
     * @param format the format of the color buffer; according to the OpenGL ES 2.0 spec, only RGB565, RGBA4444 and RGB5_A1 are
     *           color-renderable
     * @param width the width of the framebuffer in pixels
     * @param height the height of the framebuffer in pixels
     * @param hasDepth whether to attach a depth buffer
     * @throws com.badlogic.gdx.utils.GdxRuntimeException in case the FrameBuffer could not be created */
    public CompositeFrameBuffer (Pixmap.Format format, int width, int height, boolean hasDepth, boolean hasStencil) {
        super(format, width, height, hasDepth, hasStencil);
    }

    @Override
    protected Texture createColorTexture () {
        int glFormat = Pixmap.Format.toGlFormat(format);
        int glType = Pixmap.Format.toGlType(format);
        GLOnlyTextureData data = new GLOnlyTextureData(width, height, 0, glFormat, glFormat, glType);
        Texture result = new Texture(data);
        result.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        result.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
        return result;
    }

    @Override
    protected void disposeColorTexture (Texture colorTexture) {
        colorTexture.dispose();
    }

    /** See {@link GLFrameBuffer#unbind()} */
    public static void unbind () {
        GLFrameBuffer.unbind();
    }
}
