package net.ncguy.argent.entity.components;

import com.badlogic.ashley.core.Component;
import net.ncguy.argent.entity.WorldEntity;

/**
 * Created by Guy on 23/07/2016.
 */
public abstract class ArgentComponent implements Component {

    protected WorldEntity parent;

    public WorldEntity parent() { return parent; }
    public ArgentComponent parent(WorldEntity parent) { this.parent = parent; return this; }
}
