package net.ncguy.argent.utils.data.tree;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by Guy on 10/07/2016.
 */
public class DataTree<T> implements Visitable<T> {

    public final Set<DataTree<T>> children = new LinkedHashSet<>();
    public final T data;

    public DataTree(T data) {
        this.data = data;
    }

    @Override
    public void accept(Visitor<T> visitor) {
        visitor.visitData(this, this.data);
        for(DataTree<T> child : children)
            child.accept(visitor.visitVisitable(child));
    }

    public DataTree<T> child(T data) {
        for(DataTree<T> child : children) {
            if(child.data.equals(data))
                return child;
        }
        return child(new DataTree<>(data));
    }

    public DataTree<T> child(DataTree<T> child) {
        children.add(child);
        return child;
    }

    @Override
    public T data() {
        return this.data;
    }
}
