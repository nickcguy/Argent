package net.ncguy.argent.utils;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by Guy on 23/06/2016.
 */
public class SpriteCache {

    private static Texture pixel;
    public static Texture pixel() {
        if(pixel == null) {
            Pixmap map = new Pixmap(1, 1, Pixmap.Format.Alpha);
            map.setColor(1, 1, 1, 1);
            pixel = new Texture(map);
            map.dispose();
        }
        return pixel;
    }

}
