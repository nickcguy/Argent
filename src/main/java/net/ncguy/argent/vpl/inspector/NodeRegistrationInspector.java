package net.ncguy.argent.vpl.inspector;

import net.ncguy.argent.vpl.node.VPLNodePin;

import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

/**
 * Created by Guy on 09/06/2016.
 */
public class NodeRegistrationInspector extends NodeInspector {



    @Override
    public String inspect() {
        Parameter[] params = node.method().getParameters();
        if(params.length < 2) return "Insufficient parameters on method "+node.method().toString();
        String out = "";

        if(params[0].getType().isAssignableFrom(List.class)) {
            Type param1Type = ((ParameterizedType) params[0].getParameterizedType()).getActualTypeArguments()[0];
            if(!Objects.equals(param1Type.getTypeName(), VPLNodePin.class.getTypeName()))
                out += "Parameter 0 must be of type List<VPLNodePin>";
        }else{
            out += "Parameter 0 must be of type List<VPLNodePin>";
        }

        if(!params[1].getType().isAssignableFrom(VPLNodePin.class))
            out += "Parameter 1 must be of type VPLNodePin";

        return out;
    }
}
