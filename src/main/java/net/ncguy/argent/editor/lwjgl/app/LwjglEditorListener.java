package net.ncguy.argent.editor.lwjgl.app;

import com.badlogic.gdx.Game;

/**
 * Created by Guy on 04/07/2016.
 */
public class LwjglEditorListener extends Game {

    @Override
    public void create() {
        setScreen(new LwjglEditorScreen());
    }

}
