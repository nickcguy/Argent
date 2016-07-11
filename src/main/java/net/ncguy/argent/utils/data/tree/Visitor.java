package net.ncguy.argent.utils.data.tree;

/**
 * Created by Guy on 10/07/2016.
 */
public interface Visitor<T> {

    Visitor<T> visitVisitable(Visitable<T> visitable);
    void visitData(Visitable<T> visitable, T data);

}
