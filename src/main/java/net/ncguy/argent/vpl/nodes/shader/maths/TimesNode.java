package net.ncguy.argent.vpl.nodes.shader.maths;

import net.ncguy.argent.vpl.VPLGraph;
import net.ncguy.argent.vpl.annotations.NodeData;
import net.ncguy.argent.vpl.compiler.IShaderNode;
import net.ncguy.argent.vpl.nodes.shader.VPLShaderNode;

import java.lang.reflect.Method;

import static net.ncguy.argent.vpl.VPLPin.Types.*;

/**
 * Created by Guy on 10/09/2016.
 */
@NodeData(value = "Multiply",tags = "shader")
public class TimesNode extends VPLShaderNode<Float> {

    public TimesNode(VPLGraph graph) {
        super(graph);
    }

    public TimesNode(VPLGraph graph, Method method) {
        super(graph, method);
    }

    @Override
    protected void buildInput() {
        addPin(inputTable, float.class, "A", INPUT);
        addPin(inputTable, float.class, "B", INPUT);
    }

    @Override
    protected void buildOutput() {
        addPin(outputTable, float.class, "Result", OUTPUT, COMPOUND);
    }

    @Override
    protected void discernType() {
        discernType(float.class, 2);
    }

    @Override
    public void resetCache() {
        fragUsed = false;
    }
    boolean fragUsed;

    @Override
    public String getUniforms() {
        return "";
    }

    @Override
    public String getVariable(int pinId) {
        IShaderNode node0 = getNodePacker(0);
        if(node0 == null)
            return "";
        IShaderNode node1 = getNodePacker(1);
        if(node1 == null) return "";
        return String.format("(%s * %s)", node0.getVariable(this), node1.getVariable(this));
    }

    @Override
    public String getFragment() {
        if(fragUsed) return "";
        fragUsed = true;
        String frag = "";
        IShaderNode node0 = getNodePacker(0);
        if(node0 != null) frag += node0.getFragment();

        IShaderNode node1 = getNodePacker(1);
        if(node1 != null) frag += node1.getFragment();
        return frag;
    }
}
