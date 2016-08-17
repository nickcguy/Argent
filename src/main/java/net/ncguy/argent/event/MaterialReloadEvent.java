package net.ncguy.argent.event;

/**
 * Created by Guy on 16/08/2016.
 */
public class MaterialReloadEvent extends AbstractEvent {

    public static interface MaterialReloadListener {
        @Subscribe
        public void onMaterialReload(MaterialReloadEvent event);
    }

}
