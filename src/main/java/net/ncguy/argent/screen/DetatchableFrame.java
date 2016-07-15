package net.ncguy.argent.screen;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;

/**
 * Created by Guy on 14/07/2016.
 */
public class DetatchableFrame {

    public Lwjgl3Window primaryWindow;

    public DetatchableFrame(Lwjgl3Window primaryWindow) {
        this.primaryWindow = primaryWindow;
    }

    public DetatchableFrame() {

    }

}
