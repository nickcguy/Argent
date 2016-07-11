package net.ncguy.argent.utils.data.tree;

/**
 * Created by Guy on 10/07/2016.
 */
public interface Visitable<T> {

    void accept(Visitor<T> visitor);
    T data();

}
