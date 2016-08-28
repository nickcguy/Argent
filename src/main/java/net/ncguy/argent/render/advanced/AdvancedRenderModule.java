package net.ncguy.argent.render.advanced;

import net.ncguy.argent.IModule;
import net.ncguy.argent.render.BasicRenderModule;
import net.ncguy.argent.world.ProjectModule;

/**
 * Created by Guy on 15/07/2016.
 */
public class AdvancedRenderModule extends IModule {



    @Override
    public String moduleName() {
        return "Advanced Renderer";
    }

    @Override
    public Class<IModule>[] dependencies() {
        return new Class[]{BasicRenderModule.class, ProjectModule.class};
    }

}
