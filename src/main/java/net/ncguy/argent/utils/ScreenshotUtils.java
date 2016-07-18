package net.ncguy.argent.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.ScreenUtils;

import java.nio.ByteBuffer;

/**
 * Created by Guy on 18/07/2016.
 */
public class ScreenshotUtils {

    private static int counter = 1;

    public static void saveScreenshot(final int w, final int h, final String prefix) {
        try{
            FileHandle fh;
            do {
                fh = Gdx.files.local("ss/"+prefix+"_"+(counter++)+".png");
            }while(fh.exists());
            final Pixmap map = getScreenshot(0, 0, w, h, true);
            PixmapIO.writePNG(fh, map);
            map.dispose();
            Gdx.app.log("SS", "Screenshot saved to " + fh);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Pixmap getScreenshot(final int x, final int y, final int w, final int h, final boolean yDown) {
        final Pixmap pixmap = ScreenUtils.getFrameBufferPixmap(x, y, w, h);

        if (yDown)
        {
            // Flip the pixmap upside down
            final ByteBuffer pixels = pixmap.getPixels();
            final int numBytes = w * h * 4;
            final byte[] lines = new byte[numBytes];
            final int numBytesPerLine = w * 4;
            for (int i = 0; i < h; i++)
            {
                pixels.position((h - i - 1) * numBytesPerLine);
                pixels.get(lines, i * numBytesPerLine, numBytesPerLine);
            }
            pixels.clear();
            pixels.put(lines);
        }

        return pixmap;
    }

}
