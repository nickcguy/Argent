package net.ncguy.argent.event.shader;

import net.ncguy.argent.event.AbstractEvent;
import net.ncguy.argent.event.Subscribe;

/**
 * Created by Guy on 16/09/2016.
 */
public class RefreshFBOEvent extends AbstractEvent {

    public static interface RefreshFBOListener {
        @Subscribe
        public void onRefreshFBO(RefreshFBOEvent event);
    }

}
