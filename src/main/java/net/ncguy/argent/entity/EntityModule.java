package net.ncguy.argent.entity;

import net.ncguy.argent.IModule;
import net.ncguy.argent.world.WorldModule;

/**
 * Created by Guy on 15/07/2016.
 */
public class EntityModule extends IModule {

    @Override
    public String moduleName() {
        return "Entity";
    }

    @Override
    public Class<IModule>[] dependencies() {
        return new Class[]{WorldModule.class};
    }
}
