package net.ncguy.argent.vpl.compiler;

import net.ncguy.argent.vpl.VPLNode;

/**
 * Created by Guy on 29/08/2016.
 */
public interface IShaderNode {

    default String getVariable(VPLNode invoker) {
        int index = 0;
        if(this instanceof VPLNode)
            index = ((VPLNode)this).getPinIndex(invoker);
        return getVariable(index);
    }

    default String getVariable() {
        return getVariable(0);
    }

    String getUniforms();
    String getVariable(int pinId);
    String getFragment();

    default boolean singleUseFragment() {
        return false;
    }

    default void resetStaticCache() {}
    default void resetCache() {}

}
