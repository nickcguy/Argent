package net.ncguy.argent.event;

import net.ncguy.argent.entity.WorldEntity;

/**
 * Created by Guy on 27/07/2016.
 */
public class WorldEntitySelectedEvent extends AbstractEvent {

    private WorldEntity entity;

    public WorldEntitySelectedEvent(WorldEntity entity) {
        this.entity = entity;
    }

    public WorldEntity getEntity() { return entity; }
    public void setEntity(WorldEntity entity) { this.entity = entity; }

    public static interface WorldEntitySelectedListener {
        @Subscribe
        void onWorldEntitySelected(WorldEntitySelectedEvent event);
    }

}
