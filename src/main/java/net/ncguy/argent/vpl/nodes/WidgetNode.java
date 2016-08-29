package net.ncguy.argent.vpl.nodes;

import net.ncguy.argent.vpl.VPLGraph;
import net.ncguy.argent.vpl.VPLNode;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Guy on 28/08/2016.
 */
public abstract class WidgetNode<T> extends VPLNode<T> {

    public WidgetNode(VPLGraph graph) {
        super(graph, null);
    }

    @Override public abstract T fetchData(VPLNode node) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException;
}
