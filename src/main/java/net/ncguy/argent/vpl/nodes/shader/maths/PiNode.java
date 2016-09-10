package net.ncguy.argent.vpl.nodes.shader.maths;

import net.ncguy.argent.vpl.VPLGraph;
import net.ncguy.argent.vpl.VPLPin;
import net.ncguy.argent.vpl.annotations.NodeData;
import net.ncguy.argent.vpl.nodes.shader.VPLShaderNode;

import java.lang.reflect.Method;

/**
 * Created by Guy on 10/09/2016.
 */
@NodeData("PI")
public class PiNode extends VPLShaderNode<Float> {

    public PiNode(VPLGraph graph) {
        super(graph);
    }

    public PiNode(VPLGraph graph, Method method) {
        super(graph, method);
    }

    @Override
    protected void buildInput() {

    }

    @Override
    protected void buildOutput() {
        addPin(outputTable, float.class, "PI", VPLPin.Types.OUTPUT, VPLPin.Types.COMPOUND);
    }

    @Override
    protected void discernType() {
        discernType(float.class, 0);
    }

    @Override
    public void resetStaticCache() {
        uniformUsed = false;
    }
    static boolean uniformUsed;

    @Override
    public String getUniforms() {
        if(uniformUsed) return "";
        uniformUsed = true;
        return "#define M_PI 3.1415926535897932384626433832795";
    }

    @Override
    public String getVariable(int pinId) {
        return "M_PI";
    }

    @Override
    public String getFragment() {
        return "";
    }
}
