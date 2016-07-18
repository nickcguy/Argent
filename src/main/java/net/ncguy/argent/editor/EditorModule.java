package net.ncguy.argent.editor;

import net.ncguy.argent.IModule;
import net.ncguy.argent.entity.EntityModule;
import net.ncguy.argent.ui.UIModule;
import net.ncguy.argent.world.WorldModule;

/**
 * Created by Guy on 17/07/2016.
 */
public class EditorModule extends IModule {

    @Override
    public String moduleName() {
        return "Editor";
    }

    @Override
    public Class<IModule>[] dependencies() {
        return new Class[]{WorldModule.class, EntityModule.class, UIModule.class};
    }
}
