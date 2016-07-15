package net.ncguy.argent.data.tree;

/**
 * Created by Guy on 15/07/2016.
 */
public interface Visitable<T> {

    void accept(Visitor<T> visitor);
    T data();

}
