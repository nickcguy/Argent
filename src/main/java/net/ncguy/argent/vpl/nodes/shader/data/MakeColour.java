package net.ncguy.argent.vpl.nodes.shader.data;

import com.badlogic.gdx.graphics.Color;
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
@NodeData(value = "Make Colour", execIn = false, execOut = false, tags = "shader")
public class MakeColour extends VPLShaderNode<Color> {

    public MakeColour(VPLGraph graph) {
        super(graph);
    }

    public MakeColour(VPLGraph graph, Method method) {
        super(graph, method);
    }

    @Override
    protected void buildInput() {
        addPin(inputTable, float.class, "R", VPLPin.Types.INPUT);
        addPin(inputTable, float.class, "G", VPLPin.Types.INPUT);
        addPin(inputTable, float.class, "B", VPLPin.Types.INPUT);
        addPin(inputTable, float.class, "A", VPLPin.Types.INPUT);
    }

    @Override
    protected void buildOutput() {
        addPin(outputTable, Color.class, "Colour", OUTPUT, COMPOUND);
    }

    @Override
    protected void discernType() {
        discernType(Color.class, 1);
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
        return "vec4 "+getVariable()+";";
    }

    @Override
    public String getVariable(int pinId) {
        return "Colour"+id;
    }

    @Override
    public String getFragment() {
        if(fragmentUsed) return "";
        fragmentUsed = true;
        IShaderNode r = getNodePacker(0);
        IShaderNode g = getNodePacker(1);
        IShaderNode b = getNodePacker(2);
        IShaderNode a = getNodePacker(3);

        String rS = "0.0", gS = "0.0", bS = "0.0", aS = "0.0";
        String pre = "";
        if(r != null) {
            pre += r.getFragment();
            rS = r.getVariable(this);
        }
        if(g != null) {
            pre += g.getFragment();
            gS = g.getVariable(this);
        }
        if(b != null) {
            pre += b.getFragment();
            bS = b.getVariable(this);
        }
        if(a != null) {
            pre += a.getFragment();
            aS = a.getVariable(this);
        }
        return String.format("%s%s = vec4(%s, %s, %s, %s);", pre, getVariable(), rS, gS, bS, aS);
    }
}
