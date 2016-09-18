package net.ncguy;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import net.ncguy.argent.Argent;
import net.ncguy.argent.ArgentGame;
import net.ncguy.argent.project.ProjectSelectorScreen;
import net.ncguy.physics.PhysicsCore;
import net.ncguy.physics.runtime.argent.ArgentPhysicsRuntime;
import net.ncguy.screen.LoaderScreen;

/**
 * Created by Guy on 15/07/2016.
 */
public class ArgentSample extends ArgentGame {

    @Override
    public void create() {
        super.create();

        PhysicsCore.loadRuntime(new ArgentPhysicsRuntime());

        Argent.loadDefaultModules();
        setScreen(new LoaderScreen(this, new ProjectSelectorScreen()));
    }

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration cfg = new Lwjgl3ApplicationConfiguration();
        cfg.useOpenGL3(true, 4, 5);
        cfg.setWindowedMode(800, 600);
        cfg.setBackBufferConfig(8, 8, 8, 8, 16, 0, 16);
        cfg.useVsync(false);
        new Lwjgl3Application(new ArgentSample(), cfg);
    }

    public static class TreeTest {
        public String cat;
        public String name;

        public TreeTest(String cat, String name) {
            this.cat = cat;
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }



}
