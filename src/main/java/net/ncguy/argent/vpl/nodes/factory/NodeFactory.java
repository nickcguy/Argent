package net.ncguy.argent.vpl.nodes.factory;

import net.ncguy.argent.vpl.VPLGraph;
import net.ncguy.argent.vpl.VPLNode;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Guy on 28/08/2016.
 */
public class NodeFactory {

    protected Class<? extends VPLNode> cls;

    public NodeFactory(Class<? extends VPLNode> cls) {
        this.cls = cls;
    }

    public VPLNode construct(VPLGraph graph) {
        try {
            return this.cls.getConstructor(VPLGraph.class).newInstance(graph);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

}
