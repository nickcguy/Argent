package net.ncguy.argent.vpl;

import net.ncguy.argent.Argent;
import net.ncguy.argent.IModule;
import net.ncguy.argent.ui.UIModule;

/**
 * Created by Guy on 18/08/2016.
 */
public class VPLModule extends IModule {

    @Override
    public void init() {
        Argent.vpl = VPLManager.instance();
    }

    @Override
    public Class<IModule>[] dependencies() {
        return new Class[]{UIModule.class};
    }

    @Override
    public String moduleName() {
        return "Visual Programming Language";
    }
}
