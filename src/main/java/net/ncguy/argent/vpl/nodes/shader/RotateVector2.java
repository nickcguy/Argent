package net.ncguy.argent.vpl.nodes.shader;

import com.badlogic.gdx.math.Vector2;
import net.ncguy.argent.vpl.VPLGraph;
import net.ncguy.argent.vpl.VPLNode;
import net.ncguy.argent.vpl.VPLPin;
import net.ncguy.argent.vpl.annotations.NodeData;
import net.ncguy.argent.vpl.compiler.IShaderNode;

import java.lang.reflect.Method;

/**
 * Created by Guy on 07/09/2016.
 */
@NodeData(value = "Rotate Vector2", execIn = false, execOut = false, tags = "shader")
public class RotateVector2 extends VPLNode<Vector2> implements IShaderNode {

    public RotateVector2(VPLGraph graph) {
        super(graph);
    }

    public RotateVector2(VPLGraph graph, Method method) {
        super(graph, method);
    }

    @Override
    protected void discernType() {
        discernType(Vector2.class, 1);
    }

    @Override
    protected void buildInput() {
        addPin(inputTable, Vector2.class, "Input", VPLPin.Types.INPUT);
        addPin(inputTable, float.class, "degrees", VPLPin.Types.INPUT);
    }

    @Override
    protected void buildOutput() {
        addPin(outputTable, Vector2.class, "Output", VPLPin.Types.OUTPUT, VPLPin.Types.COMPOUND);
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
        String s = "";
        s += "vec2 rotate(vec2 i, float degrees) {\n";
        s += "\tfloat rad = radians(degrees);\n";
        s += "\tfloat cos = cos(rad);\n";
        s += "\tfloat sin = sin(rad);\n";
        s += "\tfloat x = i.x * cos - i.y * sin;\n";
        s += "\tfloat y = i.x * sin + i.y * cos;\n";
        s += "\treturn mod(vec2(x, y), vec2(1.0));\n";
        s += "}\n";
        return s;
    }

    @Override
    public String getVariable(int pinId) {
        IShaderNode input = getNodePacker(0);
        if(input == null) return "";
        IShaderNode angle = getNodePacker(1);
        if(angle == null) return "";

        return String.format("rotate(%s, %s)", input.getVariable(this), angle.getVariable(this));
    }

    @Override
    public String getFragment() {
        IShaderNode input = getNodePacker(0);
        if(input == null) return "";
        IShaderNode angle = getNodePacker(1);
        if(angle == null) return "";

        String inPre = input.getFragment();
        String anPre = angle.getFragment();
        return String.format("%s\n\t%s\n", inPre, anPre);
    }
}
