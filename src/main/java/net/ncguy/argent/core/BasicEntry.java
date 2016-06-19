package net.ncguy.argent.core;

import net.ncguy.argent.network.Replicatable;

import java.util.Map;

/**
 * Created by Guy on 15/06/2016.
 */
@Replicatable
public class BasicEntry<T, U> implements Map.Entry<T, U> {

    public BasicEntry() {
        this(null, null);
    }

    public T key;
    public U val;

    public BasicEntry(T key, U val) {
        this.key = key;
        this.val = val;
    }

    @Override
    public T getKey() {
        return key;
    }

    @Override
    public U getValue() {
        return val;
    }

    @Override
    public U setValue(U value) {
        U oldVal = val;
        val = value;
        return oldVal;
    }
}
