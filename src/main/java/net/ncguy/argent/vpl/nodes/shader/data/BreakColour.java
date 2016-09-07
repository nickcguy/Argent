package net.ncguy.argent.vpl.nodes.shader.data;

import com.badlogic.gdx.graphics.Color;
import net.ncguy.argent.vpl.VPLGraph;
import net.ncguy.argent.vpl.annotations.NodeData;
import net.ncguy.argent.vpl.compiler.IShaderNode;
import net.ncguy.argent.vpl.nodes.shader.VPLShaderNode;

import java.lang.reflect.Method;

import static net.ncguy.argent.vpl.VPLPin.Types.*;

/**
 * Created by Guy on 07/09/2016.
 */
@NodeData(value = "Break Vector3", execIn = false, execOut = false, tags = "shader")
public class BreakColour extends VPLShaderNode<Float> {

    public BreakColour(VPLGraph graph) {
        super(graph);
    }

    public BreakColour(VPLGraph graph, Method method) {
        super(graph, method);
    }

    @Override
    protected void buildInput() {
        addPin(inputTable, Color.class, "Colour", INPUT);
    }

    @Override
    protected void buildOutput() {
        addPin(outputTable, float.class, "R", OUTPUT, COMPOUND);
        addPin(outputTable, float.class, "G", OUTPUT, COMPOUND);
        addPin(outputTable, float.class, "B", OUTPUT, COMPOUND);
        addPin(outputTable, float.class, "A", OUTPUT, COMPOUND);
    }

    @Override
    protected void discernType() {
        discernType(float.class, 4);
    }

    @Override
    public String getUniforms() {
        return "";
    }

    @Override
    public String getVariable(int pinId) {
        IShaderNode node = getNodePacker(0);
        if(node == null) return "";
        switch(pinId) {
            case 0: return node.getVariable(this)+".r";
            case 1: return node.getVariable(this)+".g";
            case 2: return node.getVariable(this)+".b";
            case 3: return node.getVariable(this)+".a";
        }
        return "";
    }

    @Override
    public String getFragment() {
        IShaderNode node = getNodePacker(0);
        if(node == null) return "";
        return node.getFragment();
    }
}
