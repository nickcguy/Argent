package net.ncguy.argent.vpl.nodes.shader;

import net.ncguy.argent.vpl.VPLGraph;
import net.ncguy.argent.vpl.VPLNode;
import net.ncguy.argent.vpl.annotations.NodeData;
import net.ncguy.argent.vpl.compiler.IShaderNode;

import java.lang.reflect.Method;

import static net.ncguy.argent.vpl.VPLPin.Types.*;

/**
 * Created by Guy on 31/08/2016.
 */
@NodeData(value = "1 - ", execIn = false, execOut = false, tags = "shader")
public class OneMinusNode extends VPLNode<Object> implements IShaderNode {

    public OneMinusNode(VPLGraph graph, Method method) {
        this(graph);
    }

    public OneMinusNode(VPLGraph graph) {
        super(graph, null);
    }

    @Override
    protected void discernType() {
        discernType(Object.class, 1);
    }

    @Override
    protected void buildInput() {
        addPin(inputTable, Object.class, "", INPUT);
    }

    @Override
    protected void buildOutput() {
        addPin(outputTable, Object.class, "1-", OUTPUT, COMPOUND);
    }

    // Shader Stuff

    public static int globalColourId = 0;

    int colId;

    @Override
    public void resetCache() {
        colId = globalColourId++;
    }

    @Override
    public String getUniforms() {
//        return "vec4 colour"+colId+";";
        return "";
    }

    @Override
    public String getVariable(int pinId) {
        VPLNode node = getInputNodeAtPin(0);
        if(!(node instanceof IShaderNode))
            return "Error//";
        return "(1 - "+((IShaderNode) node).getVariable(this)+")";
    }

    @Override
    public String getFragment() {
        return "";
    }

}
