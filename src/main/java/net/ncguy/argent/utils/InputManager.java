package net.ncguy.argent.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;

/**
 * Created by Guy on 30/07/2016.
 */
public class InputManager extends InputMultiplexer {

    public InputManager() {
        super();
        Gdx.input.setInputProcessor(this);
    }

    public InputManager(InputProcessor... processors) {
        super(processors);
        Gdx.input.setInputProcessor(this);
    }
}
