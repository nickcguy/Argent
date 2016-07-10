package net.ncguy.argent.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Guy on 04/07/2016.
 */
public class AppUtils {

    public static class Graphics {

        public static Vector2 getPackedSize() {
            return new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }

        public static Vector2 getPackedBufferSize() {
            return new Vector2(Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
        }

        public static Vector2 getPackedCursorPos() {
            return new Vector2(Gdx.input.getX(), Gdx.graphics.getHeight()-Gdx.input.getY());
        }

    }

}
