package net.ncguy.argent;

import com.badlogic.gdx.Game;

/**
 * Created by Guy on 15/07/2016.
 */
public abstract class ArgentGame extends Game {

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        Argent.onResize(width, height);
    }
}
