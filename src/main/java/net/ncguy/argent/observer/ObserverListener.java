package net.ncguy.argent.observer;

/**
 * Created by Guy on 15/06/2016.
 */
public interface ObserverListener<T> {

    void onChange(T oldVal, T newVal);

}
