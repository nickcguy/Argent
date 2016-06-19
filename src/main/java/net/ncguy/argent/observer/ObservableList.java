package net.ncguy.argent.observer;

import net.ncguy.argent.network.Replicatable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Guy on 15/06/2016.
 */
@Replicatable
public class ObservableList<T> extends ArrayList<T> implements IObservable<T> {

    private transient List<ObserverListener<T>> listeners;

    public ObservableList() {
        this.listeners = new ArrayList<>();
    }

    @Override public void addListener(ObserverListener<T> listener)    { this.listeners.add(listener);    }
    @Override public void removeListener(ObserverListener<T> listener) { this.listeners.remove(listener); }

//    public void set(T value) {
//        this.listeners.forEach(l -> l.onChange(this.value, value));
//        this.value = value;
//    }


    @Override
    public boolean add(T t) {
        this.listeners.forEach(l -> l.onChange("list.add", t));
        return super.add(t);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        c.forEach(this::add);
        return true;
    }
}
