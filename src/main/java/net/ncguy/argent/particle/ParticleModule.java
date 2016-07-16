package net.ncguy.argent.particle;

import net.ncguy.argent.IModule;
import net.ncguy.argent.entity.EntityModule;
import net.ncguy.argent.render.BasicRenderModule;
import net.ncguy.argent.world.WorldModule;

/**
 * Created by Guy on 16/07/2016.
 */
public class ParticleModule extends IModule {

    @Override
    public String moduleName() {
        return "Particles";
    }

    @Override
    public Class<IModule>[] dependencies() {
        return new Class[]{WorldModule.class, BasicRenderModule.class, EntityModule.class};
    }
}
