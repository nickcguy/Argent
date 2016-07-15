package net.ncguy.argent.screen;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;

/**
 * Created by Guy on 14/07/2016.
 */
public interface IDetatchableScreen extends Screen {

    void attach(Lwjgl3Window window);
    Lwjgl3Window detatch();

}
