package net.ncguy.argent.utils;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Guy on 18/07/2016.
 */
public class TextureCache {

    private static Texture white;
    public static Texture white() {
        if(white == null) {
            Pixmap map = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            map.setColor(1, 1, 1, 1);
            map.drawPixel(0, 0);
            white = new Texture(map);
            map.dispose();
        }
        return white;
    }

    private static Texture black;
    public static Texture black() {
        if(black == null) {
            Pixmap map = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            map.setColor(0, 0, 0, 1);
            map.drawPixel(0, 0);
            black = new Texture(map);
            map.dispose();
        }
        return black;
    }

    public static Texture get(String ref) {
        ref = ref.toLowerCase();
        try {
            Method method = TextureCache.class.getDeclaredMethod(ref);
            if(method != null) {
                if(method.getReturnType() == Texture.class)
                    return (Texture) method.invoke(null);
            }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return white();
    }
}
