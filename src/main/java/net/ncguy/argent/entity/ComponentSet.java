package net.ncguy.argent.entity;

import net.ncguy.argent.entity.components.ArgentComponent;

import java.util.Set;

/**
 * Created by Guy on 29/07/2016.
 */
public class ComponentSet {
    Set<Class<? extends ArgentComponent>> set;

    public ComponentSet(Set<Class<? extends ArgentComponent>> set) {
            this.set = set;
        }

    public Set<Class<? extends ArgentComponent>> set() {
            return set;
        }
}
