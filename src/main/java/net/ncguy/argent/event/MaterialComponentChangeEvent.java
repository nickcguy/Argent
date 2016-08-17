package net.ncguy.argent.event;

/**
 * Created by Guy on 05/08/2016.
 */
public class MaterialComponentChangeEvent extends AbstractEvent {

    public final MaterialComponentChangeEvent.ChangeType type;

    public MaterialComponentChangeEvent(MaterialComponentChangeEvent.ChangeType type) {
        this.type = type;
    }

    public static interface MaterialComponentChangeListener {
        @Subscribe
        void onMaterialComponentChange(MaterialComponentChangeEvent event);
    }

    public static enum ChangeType {
        ADD, REMOVE
    }

}
