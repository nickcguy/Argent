package net.ncguy.argent.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guy on 15/06/2016.
 */
public class ObserverWrapper<T> {

    private T value;
    private List<ObserverListener<T>> listeners;

    public ObserverWrapper(T value) {
        this.value = value;
        this.listeners = new ArrayList<>();
    }

    public void addListener(ObserverListener<T> listener)    { this.listeners.add(listener);    }
    public void removeListener(ObserverListener<T> listener) { this.listeners.remove(listener); }

    public T get() { return value; }

    public void set(T value) {
        this.listeners.forEach(l -> l.onChange(this.value, value));
        this.value = value;
    }
}
