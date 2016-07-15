package net.ncguy.argent.data.tree;

/**
 * Created by Guy on 15/07/2016.
 */
public class TreeObjectWrapper<T> {

    public T object;
    public String label;

    public TreeObjectWrapper(String label) {
        this(null, label);
    }

    public TreeObjectWrapper(T object, String label) {
        this.object = object;
        this.label = label;
    }

    @Override
    public String toString() {
        if(object == null)
            return label;
        return object.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(this.object != null) if(this.object.equals(obj)) return true;
        if(this.label.equals(obj)) return true;
        if(obj instanceof TreeObjectWrapper) {
            TreeObjectWrapper o = (TreeObjectWrapper)obj;
            if(this.object != null) if(this.object.equals(o.object)) return true;
            if(this.label.equals(o.label)) return true;
        }
        return super.equals(obj);
    }


}
