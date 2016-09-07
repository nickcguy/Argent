package net.ncguy.argent.vpl.nodes.shader;

import net.ncguy.argent.vpl.VPLGraph;
import net.ncguy.argent.vpl.VPLNode;
import net.ncguy.argent.vpl.compiler.IShaderNode;

import java.lang.reflect.Method;

/**
 * Created by Guy on 07/09/2016.
 */
public abstract class VPLShaderNode<T> extends VPLNode<T> implements IShaderNode {

    public VPLShaderNode(VPLGraph graph) {
        super(graph);
    }

    public VPLShaderNode(VPLGraph graph, Method method) {
        super(graph, method);
    }

    @Override protected abstract void buildInput();
    @Override protected abstract void buildOutput();
    @Override protected abstract void discernType();
}
