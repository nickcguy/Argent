package net.ncguy.argent.vpl.node;

import net.ncguy.argent.core.Meta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        this.pins = new ArrayList<>();
    }

    public void buildNode() {

    }

    public Object invokeFromPin(VPLNodePin instigator) throws InvocationTargetException, IllegalAccessException {
        List<VPLNodePin> inPins = pins.stream().filter(p -> p.hasFlag(VPLNodePin.FLAGS.INPUT)).collect(Collectors.toList());
        Object[] args = new Object[method.getParameterCount()];
        args[0] = pins;
        args[1] = instigator;
        final int[] index = {2};
        inPins.forEach(pin -> args[index[0]++] = pin.getNodeValue());
        return method.invoke(null, args);
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
