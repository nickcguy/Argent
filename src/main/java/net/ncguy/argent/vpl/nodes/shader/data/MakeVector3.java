package net.ncguy.argent.vpl.nodes.shader.data;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
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
@NodeData(value = "Make Vector3", execIn = false, execOut = false, tags = "shader")
public class MakeVector3 extends VPLShaderNode<Vector3> {

    public MakeVector3(VPLGraph graph) {
        super(graph);
    }

    public MakeVector3(VPLGraph graph, Method method) {
        super(graph, method);
    }

    @Override
    protected void buildInput() {
        addPin(inputTable, float.class, "X", VPLPin.Types.INPUT);
        addPin(inputTable, float.class, "Y", VPLPin.Types.INPUT);
        addPin(inputTable, float.class, "Z", VPLPin.Types.INPUT);
    }

    @Override
    protected void buildOutput() {
        addPin(outputTable, Vector3.class, "Vector3", OUTPUT, COMPOUND);
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
        return "vec3 "+getVariable()+";";
    }

    @Override
    public String getVariable(int pinId) {
        return "Vector3"+id;
    }

    @Override
    public String getFragment() {
        if(fragmentUsed) return "";
        fragmentUsed = true;
        IShaderNode x = getNodePacker(0);
        IShaderNode y = getNodePacker(1);
        IShaderNode z = getNodePacker(2);

        String xS = "0.0", yS = "0.0", zS = "0.0";
        String pre = "";
        if(x != null) {
            pre += x.getFragment();
            xS = x.getVariable(this);
        }
        if(y != null) {
            pre += y.getFragment();
            yS = y.getVariable(this);
        }
        if(z != null) {
            pre += z.getFragment();
            zS = z.getVariable(this);
        }
        return String.format("%s%s = vec3(%s, %s, %s);", pre, getVariable(), xS, yS, zS);
    }
}
