package net.ncguy.argent.vpl.inspector;

import net.ncguy.argent.vpl.node.VPLBaseNode;

/**
 * Created by Guy on 09/06/2016.
 */
public abstract class NodeInspector implements IInspector {

    protected VPLBaseNode node;

    public String inspect(VPLBaseNode node) {
        this.node = node;
        return inspect();
    }

}
