package net.ncguy.argent.vpl.nodes.shader.data;

import com.badlogic.gdx.math.Vector2;
import net.ncguy.argent.vpl.VPLGraph;
import net.ncguy.argent.vpl.VPLPin;
import net.ncguy.argent.vpl.annotations.NodeData;
import net.ncguy.argent.vpl.compiler.IShaderNode;
import net.ncguy.argent.vpl.nodes.shader.VPLShaderNode;

import java.lang.reflect.Method;

import static net.ncguy.argent.vpl.VPLPin.Types.COMPOUND;
import static net.ncguy.argent.vpl.VPLPin.Types.OUTPUT;

/**
 * Created by Guy on 07/09/2016.
 */
@NodeData(value = "Make Vector2", execIn = false, execOut = false, tags = "shader")
public class MakeVector2 extends VPLShaderNode<Vector2> {

    public MakeVector2(VPLGraph graph) {
        super(graph);
    }

    public MakeVector2(VPLGraph graph, Method method) {
        super(graph, method);
    }

    @Override
    protected void buildInput() {
        addPin(inputTable, float.class, "X", VPLPin.Types.INPUT);
        addPin(inputTable, float.class, "Y", VPLPin.Types.INPUT);
    }

    @Override
    protected void buildOutput() {
        addPin(outputTable, Vector2.class, "Vector2", OUTPUT, COMPOUND);
    }

    @Override
    protected void discernType() {
        discernType(Vector2.class, 1);
    }

    @Override
    public void resetStaticCache() {
        globalId = 0;
    }
    static int globalId;

    @Override
    public void resetCache() {
        id = globalId++;
        fragmentUsed = false;
    }

    int id;
    boolean fragmentUsed;

    @Override
    public String getUniforms() {
        return "vec2 "+getVariable()+";";
    }

    @Override
    public String getVariable(int pinId) {
        return "Vector2"+id;
    }

    @Override
    public String getFragment() {
        if(fragmentUsed) return "";
        fragmentUsed = true;
        IShaderNode x = getNodePacker(0);
        IShaderNode y = getNodePacker(1);

        String xS = "0.0", yS = "0.0";
        String pre = "";
        if(x != null) {
            pre += x.getFragment();
            xS = x.getVariable(this);
        }
        if(y != null) {
            pre += y.getFragment();
            yS = y.getVariable(this);
        }
        return String.format("%s%s = vec2(%s, %s);", pre, getVariable(), xS, yS);
    }
}
