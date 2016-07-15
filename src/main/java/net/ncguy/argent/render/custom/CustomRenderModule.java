package net.ncguy.argent.render.custom;

import net.ncguy.argent.IModule;
import net.ncguy.argent.render.BasicRenderModule;
import net.ncguy.argent.world.WorldModule;

/**
 * Created by Guy on 15/07/2016.
 */
public class CustomRenderModule extends IModule {

    @Override
    public String moduleName() {
        return "Custom Renderer";
    }

    @Override
    public Class<IModule>[] dependencies() {
        return new Class[]{WorldModule.class, BasicRenderModule.class};
    }
}
