package net.ncguy.argent.observer;

/**
 * Created by Guy on 15/06/2016.
 */
public interface ObserverListener<T> {

    void onChange(String identifier, T newVal);

}
