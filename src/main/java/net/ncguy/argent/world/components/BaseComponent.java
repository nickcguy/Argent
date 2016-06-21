package net.ncguy.argent.world.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool;

/**
 * Created by Guy on 21/06/2016.
 */
public abstract class BaseComponent implements Component, Pool.Poolable {

    public Entity parent;

    public BaseComponent attachComponent(Entity entity) {
        this.parent = entity;
        return this;
    }

}
