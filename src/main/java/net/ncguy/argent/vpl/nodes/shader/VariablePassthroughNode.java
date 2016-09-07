package net.ncguy.argent.vpl.nodes.shader;

import net.ncguy.argent.vpl.VPLGraph;
import net.ncguy.argent.vpl.VPLManager;
import net.ncguy.argent.vpl.VPLNode;
import net.ncguy.argent.vpl.annotations.NodeData;
import net.ncguy.argent.vpl.compiler.IShaderNode;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static net.ncguy.argent.vpl.VPLPin.Types.*;

/**
 * Created by Guy on 31/08/2016.
 */
@NodeData(value = "Passthrough", execIn = false, execOut = false, tags = "shader")
public class VariablePassthroughNode extends VPLNode<Object> implements IShaderNode {

    public VariablePassthroughNode(VPLGraph graph, Method method) {
        this(graph);
    }

    public VariablePassthroughNode(VPLGraph graph) {
        super(graph, null);
    }

    @Override
    public String getTitle() {
        return "";
    }

    @Override
    protected void buildTable() {
        this.width = 0;
        super.buildTable();
    }

    @Override
    public void invokeSelf(int pin) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
    }

    @Override
    public Object fetchData(VPLNode node) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        return null;
    }

    @Override protected void buildInput() {
        addPin(inputTable, Object.class, "", INPUT);
    }

    @Override
    protected void discernType() {
        discernType(Object.class, 0);
    }

    @Override
    protected void buildOutput() {
        String[] argNames = VPLManager.instance().getArgNames(this);
        Class<?>[] argTypes = VPLManager.instance().getOutputTypes(this);
        for (int i = 0; i < argNames.length; i++)
            addPin(outputTable, argTypes[i], argNames[i], OUTPUT, COMPOUND);
    }

    @Override
    public String getUniforms() {
        return "";
    }

    @Override
    public String getVariable(int pinId) {
        VPLNode node = getInputNodeAtPin(0);
        if(node == null || node == this) return "";
        if(node instanceof IShaderNode)
            return ((IShaderNode) node).getVariable(this);
        return "";
    }

    @Override
    public String getFragment() {
        IShaderNode node = getNodePacker(0);
        if(node == null) return "";
        return node.getFragment();
    }
}
