package net.ncguy.argent.vpl.nodes.shader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import net.ncguy.argent.vpl.VPLGraph;
import net.ncguy.argent.vpl.VPLNode;
import net.ncguy.argent.vpl.VPLPin;
import net.ncguy.argent.vpl.annotations.NodeData;
import net.ncguy.argent.vpl.compiler.IShaderNode;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Guy on 07/09/2016.
 */
@NodeData(value = "Panner", tags = "shader", execIn = false, execOut = false)
public class PannerNode extends VPLNode<Vector2> implements IShaderNode {

    public PannerNode(VPLGraph graph) {
        super(graph, null);
    }

    public PannerNode(VPLGraph graph, Method method) {
        super(graph, method);
    }

    @Override
    protected void buildInput() {
        addPin(inputTable, Vector2.class, "UV Source", VPLPin.Types.INPUT);
        addPin(inputTable, float.class, "U Speed [1]", VPLPin.Types.INPUT);
        addPin(inputTable, float.class, "V Speed [1]", VPLPin.Types.INPUT);
    }

    @Override
    protected void buildOutput() {
        addPin(outputTable, Vector2.class, "UV", VPLPin.Types.OUTPUT, VPLPin.Types.COMPOUND);
    }

    @Override public void invokeSelf(int pin) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {}

    @Override public Vector2 fetchData(VPLNode node) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException { return null; }

    @Override
    protected void discernType() {
        discernType(Vector2.class, 1);
    }

    // Shader Stuff


    @Override
    public void resetStaticCache() {
        globalId = 0;
    }
    static int globalId;
    int id;
    boolean fragmentUsed;
    float offset;

    @Override
    public void resetCache() {
        id = globalId++;
        fragmentUsed = false;
        offset = 0;
    }

    @Override
    public String getUniforms() {
        String s = "vec2 "+getVariable()+";\n";
        s += "uniform float u_pannerOffset"+id+";\n";
        return s;
    }

    @Override
    public String getVariable(int pinId) {
        return "PannedTexCoords"+id;
    }

    @Override
    public String getFragment() {
        if(fragmentUsed) return "";
        fragmentUsed = true;
        IShaderNode node = getNodePacker(0);
        if(node == null) return "";
        String s = getVariable() + " = "+node.getVariable(this)+";\n";

        IShaderNode uNode = getNodePacker(1);
        String u = "1.0";
        if(uNode != null) u = uNode.getVariable();
        IShaderNode vNode = getNodePacker(2);
        String v = "1.0";
        if(vNode != null) v = vNode.getVariable();
        String speed = String.format("vec2(%s, %s)", u, v);

        String pre = node.getFragment();

        s = String.format("\t%s\n\t%s = mod((%s * %s) + %s, 1.0);\n", pre, getVariable(), "u_pannerOffset"+id, speed, node.getVariable(this));
        return s;
    }

    @Override
    public void bind(ShaderProgram program) {
        offset += Gdx.graphics.getDeltaTime();
        offset %= 10f;
        program.setUniformf("u_pannerOffset"+id, offset);
    }

    @Override
    public boolean singleUseFragment() {
        return true;
    }
}
