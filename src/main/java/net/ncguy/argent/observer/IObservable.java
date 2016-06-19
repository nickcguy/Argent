package net.ncguy.argent.observer;

/**
 * Created by Guy on 15/06/2016.
 */
public interface IObservable<T> {

    void addListener(ObserverListener<T> listener);
    void removeListener(ObserverListener<T> listener);

}
