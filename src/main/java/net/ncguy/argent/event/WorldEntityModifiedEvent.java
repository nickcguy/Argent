package net.ncguy.argent.event;

import net.ncguy.argent.entity.WorldEntity;

/**
 * Created by Guy on 27/07/2016.
 */
public class WorldEntityModifiedEvent {

    private WorldEntity entity;

    public WorldEntityModifiedEvent() {}

    public WorldEntityModifiedEvent(WorldEntity entity) {
        this.entity = entity;
    }

    public WorldEntity getEntity() { return entity; }
    public void setEntity(WorldEntity entity) { this.entity = entity; }

    public static interface WorldEntityModifiedListener {
        @Subscribe
        void onWorldEntityModified(WorldEntityModifiedEvent event);
    }

}
