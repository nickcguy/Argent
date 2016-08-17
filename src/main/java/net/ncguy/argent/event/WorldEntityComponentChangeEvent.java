package net.ncguy.argent.event;

/**
 * Created by Guy on 29/07/2016.
 */
public class WorldEntityComponentChangeEvent extends AbstractEvent {

    public final ChangeType type;

    public WorldEntityComponentChangeEvent(ChangeType type) {
        this.type = type;
    }

    public static interface WorldEntityComponentChangeListener {
        @Subscribe
        void onWorldEntityComponentChange(WorldEntityComponentChangeEvent event);
    }

    public static enum ChangeType {
        ADD, REMOVE
    }

}
