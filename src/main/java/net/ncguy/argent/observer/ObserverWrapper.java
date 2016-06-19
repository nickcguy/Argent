package net.ncguy.argent.observer;

import net.ncguy.argent.network.Replicatable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guy on 15/06/2016.
 */
@Replicatable
public class ObserverWrapper<T> implements IObservable<T> {

    public T value;
    private transient List<ObserverListener<T>> listeners;

    public ObserverWrapper() {
        this(null);
    }

    public ObserverWrapper(T value) {
        this.value = value;
        this.listeners = new ArrayList<>();
    }

    @Override public void addListener(ObserverListener<T> listener)    { this.listeners.add(listener);    }
    @Override public void removeListener(ObserverListener<T> listener) { this.listeners.remove(listener); }

    public T get() { return value; }

    public void set(T value) {
        this.listeners.forEach(l -> l.onChange("set", value));
        this.value = value;
    }
}
