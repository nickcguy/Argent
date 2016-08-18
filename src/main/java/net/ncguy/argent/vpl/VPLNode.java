package net.ncguy.argent.vpl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Guy on 18/08/2016.
 */
public abstract class VPLNode<T> {

    protected Method method;
    protected boolean continuous;
    protected T value;

    public T invoke(Object... args) throws InvocationTargetException, IllegalAccessException {
        if(continuous)
            return (T) method.invoke(null, args);
        if(value == null) value = (T) method.invoke(null, args);
        return value;
    }

    public void invalidate() {
        value = null;
    }

}
