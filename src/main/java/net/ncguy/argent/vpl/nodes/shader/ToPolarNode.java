package net.ncguy.argent.vpl.nodes.shader;

import com.badlogic.gdx.math.Vector2;
import net.ncguy.argent.vpl.VPLGraph;
import net.ncguy.argent.vpl.VPLPin;
import net.ncguy.argent.vpl.annotations.NodeData;
import net.ncguy.argent.vpl.compiler.IShaderNode;

import java.lang.reflect.Method;

/**
 * Created by Guy on 07/09/2016.
 */
@NodeData(value = "Cartesian to Polar", execIn = false, execOut = false, tags = "shader")
public class ToPolarNode extends VPLShaderNode<Vector2> {

    public ToPolarNode(VPLGraph graph) {
        super(graph);
    }

    public ToPolarNode(VPLGraph graph, Method method) {
        super(graph, method);
    }

    @Override
    protected void discernType() {
        discernType(Vector2.class, 1);
    }

    @Override
    protected void buildInput() {
        addPin(inputTable, Vector2.class, "Cartesian", VPLPin.Types.INPUT);
    }

    @Override
    protected void buildOutput() {
        addPin(outputTable, Vector2.class, "Polar", VPLPin.Types.OUTPUT, VPLPin.Types.COMPOUND);
    }

    // Shader Stuff


    @Override
    public void resetStaticCache() {
        globalId = 0;
        methodDeclared = false;
        fragmentUsed = false;
    }
    static int globalId;
    int id;
    static boolean methodDeclared;
    boolean fragmentUsed;

    @Override
    public void resetCache() {
        id = globalId++;
    }

    @Override
    public String getUniforms() {
        String uniform = "";
//        uniform += "vec2 "+getVariable()+";\n";
        if(methodDeclared) return uniform;
        methodDeclared = true;
        uniform += "vec2 toPolar(vec2 cartesian) {\n";
        uniform += "\tvec2 p = cartesian - 0.5;\n";
        uniform += "\tfloat r = length(p);\n";
        uniform += "\tfloat a = atan(p.y, p.x);\n";
        uniform += "\treturn mod(vec2(r, a), vec2(1.0));\n";
        uniform += "}\n";
        return uniform;
    }

    @Override
    public String getVariable(int pinId) {
        IShaderNode uv = getNodePacker(0);
        String uvVec = uv != null ? uv.getVariable(this) : "vec2(0.0)";
        return "toPolar("+uvVec+")";
    }

    @Override
    public String getFragment() {
        IShaderNode pre = getNodePacker(0);
        if(pre == null) return "";
        return pre.getFragment();
    }

    @Override
    public boolean singleUseFragment() {
        return false;
    }
}
