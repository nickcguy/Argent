package net.ncguy.argent.injector;

import net.ncguy.argent.Argent;
import net.ncguy.argent.IModule;

/**
 * Created by Guy on 27/07/2016.
 */
public class InjectionModule extends IModule {

    @Override
    public void init() {
        Argent.injector = this;
    }

    @Override
    public String moduleName() {
        return "Injector";
    }

}
