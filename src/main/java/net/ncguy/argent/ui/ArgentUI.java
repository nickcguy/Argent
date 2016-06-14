package net.ncguy.argent.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Created by Guy on 10/06/2016.
 */
public class ArgentUI {

    private static Skin skin;

    public static void load() {
        skin = new Skin(Gdx.files.internal(""));
    }

}
