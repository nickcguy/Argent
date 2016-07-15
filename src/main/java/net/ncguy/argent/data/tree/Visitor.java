package net.ncguy.argent.data.tree;

/**
 * Created by Guy on 15/07/2016.
 */
public interface Visitor<T> {

    Visitor<T> visit(Visitable<T> visitable);
    void visitData(Visitable<T> visitable, T data);

}
