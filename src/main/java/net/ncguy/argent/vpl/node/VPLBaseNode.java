package net.ncguy.argent.vpl.node;

import net.ncguy.argent.core.Meta;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Guy on 09/06/2016.
 */
public class VPLBaseNode {

    private Method method;
    private Meta meta;
    private List<VPLNodePin> pins;

    public VPLBaseNode(Method method) {
        this.method = method;
        if(this.method.isAnnotationPresent(Meta.class))
            meta = this.method.getAnnotation(Meta.class);
    }

    public void invoke() {

    }

    public Method method() {
        return method;
    }

    @Override
    public String toString() {
        if(this.meta == null)
            return String.format("%s", method.toString());
        return String.format("%s: %s", meta.displayName(), method.toString());
    }
}
