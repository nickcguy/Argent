package net.ncguy.argent;

import net.ncguy.argent.injector.InjectionStore;
import net.ncguy.argent.utils.InputManager;

/**
 * Created by Guy on 30/07/2016.
 */
public class CoreModule extends IModule {

    @Override
    public void init() {
        super.init();
        try {
            InjectionStore.setGlobal(new InputManager());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String moduleName() {
        return "Core";
    }

}
