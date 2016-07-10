package net.ncguy.argent.view;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowConfiguration;

/**
 * Created by Guy on 04/07/2016.
 */
public class ArgentWindowFactory {

    private static Lwjgl3Application app;

    private static ArgentWindowFactory instance;
    public static ArgentWindowFactory instance() {
        if (instance == null)
            instance = new ArgentWindowFactory();
        return instance;
    }

    private ArgentWindowFactory() {
        app = (Lwjgl3Application)Gdx.app;
    }

    public Lwjgl3Window window(ApplicationListener listener, Lwjgl3WindowConfiguration cfg) {
        return app.newWindow(listener, cfg);
    }

    public Lwjgl3Window duplicateWindow(Lwjgl3WindowConfiguration cfg) {
        return app.newWindow(app.getApplicationListener(), cfg);
    }

    public Lwjgl3Window window(ApplicationListener listener) {
        return app.newWindow(listener, defaultCfg());
    }

    public Lwjgl3WindowConfiguration defaultCfg() {
        Lwjgl3WindowConfiguration cfg = new Lwjgl3WindowConfiguration();
        cfg.setWindowedMode(800, 600);
        return cfg;
    }


}
