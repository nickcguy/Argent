package net.ncguy.argent.event;

import net.ncguy.argent.Argent;
import net.ncguy.argent.IModule;

/**
 * Created by Guy on 27/07/2016.
 */
public class EventModule extends IModule {

    @Override
    public void init() {
        Argent.event = new EventBus();
    }

    @Override
    public String moduleName() {
        return "Event handler";
    }
}
