package net.ncguy.argent.vpl.compiler;

import net.ncguy.argent.vpl.VPLNode;
import net.ncguy.argent.vpl.VPLPane;

/**
 * Created by Guy on 19/08/2016.
 */
public abstract class VPLCompiler<T> {

    public abstract T compile(VPLPane pane);

    public void invokeNode(VPLNode node) {
        if(node == null) return;

    }

    public VPLNode getNextNode(VPLNode node) {
        return node.getExecOutPin().getExecNode();
    }

}
