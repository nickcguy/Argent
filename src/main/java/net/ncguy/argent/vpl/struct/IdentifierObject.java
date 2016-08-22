package net.ncguy.argent.vpl.struct;

/**
 * Created by Guy on 22/08/2016.
 */
public class IdentifierObject<T> {

    protected Class<T> cls;
    protected T value;

    public IdentifierObject(Class<T> cls, T value) {
        this.cls = cls;
        this.value = value;
    }

    public Class<T> getCls() { return cls; }

    public T value() { return value; }

    public void value(T value) { this.value = value; }
}
