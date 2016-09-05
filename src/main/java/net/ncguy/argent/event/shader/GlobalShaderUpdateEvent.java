package net.ncguy.argent.event.shader;

import net.ncguy.argent.event.AbstractEvent;
import net.ncguy.argent.event.Subscribe;

/**
 * Created by Guy on 31/08/2016.
 */
public class GlobalShaderUpdateEvent extends AbstractEvent {

    public static interface GlobalShaderUpdateListener {
        @Subscribe
        public void onGlobalShaderUpdate(GlobalShaderUpdateEvent event);
    }

}
