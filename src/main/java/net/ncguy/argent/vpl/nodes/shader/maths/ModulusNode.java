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
@NodeData(value = "Mod",tags = "shader")
public class ModulusNode extends VPLShaderNode<Float> {

    public ModulusNode(VPLGraph graph) {
        super(graph);
    }

    public ModulusNode(VPLGraph graph, Method method) {
        super(graph, method);
    }

    @Override
    protected void buildInput() {
        addPin(inputTable, float.class, "Value", INPUT);
        addPin(inputTable, float.class, "Modulus", INPUT);
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
        String mod = "0.0";
        IShaderNode node1 = getNodePacker(1);
        if(node1 != null) mod = node1.getVariable(this);
        return String.format("mod(%s, %s)", node0.getVariable(this), mod);
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
