package net.ncguy.argent.editor.swing;

import net.ncguy.argent.pipe.ObjectPipe;
import net.ncguy.argent.world.GameWorld;

/**
 * Created by Guy on 27/06/2016.
 */
public class EditorRootConfig<T> {

    public GameWorld.Generic<T> gameWorld;

    public static class Factory {
        public static <T> EditorRootConfig<T> buildConfig(GameWorld.Generic<T> world) {
            ObjectPipe.register("active.gameworld.generic", () -> world);
            EditorRootConfig<T> cfg = new EditorRootConfig<>();
            cfg.gameWorld = world;
            return cfg;
        }
    }

}
