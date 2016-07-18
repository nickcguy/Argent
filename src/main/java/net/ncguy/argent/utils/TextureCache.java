package net.ncguy.argent.utils;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by Guy on 18/07/2016.
 */
public class TextureCache {

    private static Texture pixel;
    public static Texture pixel() {
        if(pixel == null) {
            Pixmap map = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            map.setColor(1, 1, 1, 1);
            map.drawPixel(0, 0);
            pixel = new Texture(map);
            map.dispose();
        }
        return pixel;
    }

}
