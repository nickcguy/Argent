package net.ncguy.argent.vpl.nodes.shader;

import net.ncguy.argent.vpl.VPLGraph;
import net.ncguy.argent.vpl.annotations.NodeData;

import java.lang.reflect.Method;

import static net.ncguy.argent.vpl.VPLPin.Types.COMPOUND;
import static net.ncguy.argent.vpl.VPLPin.Types.OUTPUT;

/**
 * Created by Guy on 10/09/2016.
 */
@NodeData("Time")
public class GLFWTimeNode extends VPLShaderNode<Float> {

    public GLFWTimeNode(VPLGraph graph) {
        super(graph);
    }

    public GLFWTimeNode(VPLGraph graph, Method method) {
        super(graph, method);
    }

    @Override protected void buildInput() {}

    @Override
    protected void buildOutput() {
        addPin(outputTable, float.class, "Time", OUTPUT, COMPOUND);
    }

    @Override
    protected void discernType() {
        discernType(float.class, 0);
    }

    @Override
    public String getUniforms() {
        return "uniform float u_time;";
    }

    @Override
    public String getVariable(int pinId) {
        return "u_time";
    }

    @Override
    public String getFragment() {
        return "";
    }
}
