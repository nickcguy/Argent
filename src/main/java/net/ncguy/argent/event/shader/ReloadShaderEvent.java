package net.ncguy.argent.event.shader;

import net.ncguy.argent.event.AbstractEvent;
import net.ncguy.argent.event.Subscribe;

/**
 * Created by Guy on 23/12/2016.
 */
public class ReloadShaderEvent extends AbstractEvent {

    public static interface ReloadShaderListener {
        @Subscribe
        void onReloadShader(ReloadShaderEvent event);
    }

}
