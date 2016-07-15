package net.ncguy.argent.render;

import net.ncguy.argent.IModule;
import net.ncguy.argent.world.WorldModule;

/**
 * Created by Guy on 15/07/2016.
 */
public class BasicRenderModule extends IModule {

    @Override
    public Class<IModule>[] dependencies() {
        return new Class[]{WorldModule.class};
    }

    @Override
    public String moduleName() {
        return "Basic Renderer";
    }

}
