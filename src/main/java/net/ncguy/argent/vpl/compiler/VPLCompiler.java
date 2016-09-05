package net.ncguy.argent.vpl.compiler;

import net.ncguy.argent.vpl.VPLGraph;
import net.ncguy.argent.vpl.VPLNode;

/**
 * Created by Guy on 19/08/2016.
 */
public abstract class VPLCompiler<T> {

    /**
     *
     * @param graph The containing graph
     * @param rootNode the node to start the compilation from
     * @param target the compile target, can be used for mutable objects
     * @return the compiled result, or target if it is set
     */
    public abstract T compile(VPLGraph graph, VPLNode rootNode, T target);

    public void invokeNode(VPLNode node) {
        if(node == null) return;

    }

    public VPLNode getNextNode(VPLNode node) {
        return node.getExecOutPin().getExecNode();
    }

}
