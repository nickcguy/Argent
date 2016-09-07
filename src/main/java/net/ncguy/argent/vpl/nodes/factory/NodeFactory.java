package net.ncguy.argent.vpl.nodes.factory;

import net.ncguy.argent.utils.AppUtils;
import net.ncguy.argent.vpl.VPLGraph;
import net.ncguy.argent.vpl.VPLNode;
import net.ncguy.argent.vpl.annotations.NodeData;

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
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        if(cls.isAnnotationPresent(NodeData.class))
            return cls.getAnnotation(NodeData.class).value();
        return super.toString();
    }

    public boolean hasTag(String... tags) {
        for (String tag : tags)
            if(hasTag(tag)) return true;
        return false;
    }

    public boolean hasTag(String tag) {
        if(!cls.isAnnotationPresent(NodeData.class)) return false;
        String[] tags = cls.getAnnotation(NodeData.class).tags();
        return AppUtils.General.arrayContains(tags, tag);
    }

}
