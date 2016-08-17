package net.ncguy.argent.event;

/**
 * Created by Guy on 01/08/2016.
 */
public class MaterialRefreshEvent extends AbstractEvent {

    public static interface MaterialRefreshListener {
        @Subscribe
        public void onMaterialRefresh(MaterialRefreshEvent event);
    }

}
