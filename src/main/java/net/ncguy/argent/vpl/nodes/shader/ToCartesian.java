package net.ncguy.argent.vpl.nodes.shader;

import com.badlogic.gdx.math.Vector2;
import net.ncguy.argent.vpl.VPLGraph;
import net.ncguy.argent.vpl.VPLNode;
import net.ncguy.argent.vpl.annotations.NodeData;
import net.ncguy.argent.vpl.compiler.IShaderNode;

import java.lang.reflect.Method;

import static net.ncguy.argent.vpl.VPLPin.Types.*;

/**
 * Created by Guy on 07/09/2016.
 */
@NodeData(value = "Polar to Cartesian", execIn = false, execOut = false, tags = "shader")
public class ToCartesian extends VPLNode<Vector2> implements IShaderNode {

    public ToCartesian(VPLGraph graph) {
        super(graph);
    }

    public ToCartesian(VPLGraph graph, Method method) {
        super(graph, method);
    }

    @Override
    protected void discernType() {
        discernType(Vector2.class, 1);
    }

    @Override
    protected void buildInput() {
        addPin(inputTable, Vector2.class, "Polar", INPUT);
    }

    @Override
    protected void buildOutput() {
        addPin(outputTable, Vector2.class, "Cartesian", OUTPUT, COMPOUND);
    }

    // Shader Stuff


    @Override
    public void resetStaticCache() {
        methodDeclared = false;
    }

    static boolean methodDeclared;

    @Override
    public String getUniforms() {
        if(methodDeclared) return "";
        methodDeclared = true;
        String uniform = "";
        uniform += "vec2 toCartesian(vec2 polar) {\n";
        uniform += "\tfloat r = polar.x;\n";
        uniform += "\tfloat t = polar.y;\n";
        uniform += "\tfloat x = r*cos(t);\n";
        uniform += "\tfloat y = r*sin(t);\n";
        uniform += "\treturn vec2(x, y);\n";
        uniform += "}\n";
        return uniform;
    }

    @Override
    public String getVariable(int pinId) {
        IShaderNode uv = getNodePacker(0);
        String uvVec = uv != null ? uv.getVariable(this) : "vec2(0.0)";
        return "toCartesian("+uvVec+")";
    }

    @Override
    public String getFragment() {
        IShaderNode pre = getNodePacker(0);
        if(pre == null) return "";
        return pre.getFragment();
    }
}
