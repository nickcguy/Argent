package net.ncguy.argent.vpl.nodes.shader;

import com.badlogic.gdx.graphics.Color;
import net.ncguy.argent.vpl.VPLGraph;
import net.ncguy.argent.vpl.VPLNode;
import net.ncguy.argent.vpl.VPLReference;
import net.ncguy.argent.vpl.annotations.NodeData;
import net.ncguy.argent.vpl.compiler.IShaderNode;

import java.lang.reflect.Method;

import static net.ncguy.argent.vpl.VPLPin.Types.*;

/**
 * Created by Guy on 31/08/2016.
 */
@NodeData(value = "Make Colour", execIn = false, execOut = false, tags = "shader", argNames = "Colour", outputTypes = Color.class)
public class MakeColourNode extends VPLNode<Color> implements IShaderNode {

    public MakeColourNode(VPLGraph graph, Method method) {
        this(graph);
    }

    public MakeColourNode(VPLGraph graph) {
        super(graph, null);
    }

    @Override
    protected void discernType() {
        discernType(Float.class, 4);
    }

    @Override
    protected void buildInput() {
        addPin(inputTable, Float.class, "Red", INPUT).useNativeColour(false).getColour().set(VPLReference.PinColours.RED);
        addPin(inputTable, Float.class, "Green", INPUT).useNativeColour(false).getColour().set(VPLReference.PinColours.GREEN);
        addPin(inputTable, Float.class, "Blue", INPUT).useNativeColour(false).getColour().set(VPLReference.PinColours.BLUE);
        addPin(inputTable, Float.class, "Alpha", INPUT).useNativeColour(false).getColour().set(VPLReference.PinColours.ALPHA);
    }

    @Override
    protected void buildOutput() {
        addPin(outputTable, Color.class, "Colour", OUTPUT, COMPOUND);
    }

    // Shader Stuff

    public static int globalColourId = 0;

    boolean fragmentUsed = false;
    int colId;

    @Override
    public void resetStaticCache() {
        globalColourId = 0;
    }

    @Override
    public void resetCache() {
        colId = globalColourId++;
    }

    @Override
    public String getUniforms() {
        return "vec4 colour"+colId+";";
    }

    @Override
    public String getVariable(int pinId) {
        return "colour"+colId;
    }

    @Override
    public String getFragment() {
        VPLNode noder = getInputNodeAtPin(0);
        VPLNode nodeg = getInputNodeAtPin(1);
        VPLNode nodeb = getInputNodeAtPin(2);
        VPLNode nodea = getInputNodeAtPin(3);

        VPLNode[] nodes = new VPLNode[]{noder, nodeg, nodeb, nodea};

        for (VPLNode node : nodes) {
            if(!(node instanceof IShaderNode))
                return "ERROR, Crash here please//";
        }

        String var = getVariable();
        String[] args = new String[5];
        args[0] = var;
        for (int i = 0; i < nodes.length; i++)
            args[i+1] = ((IShaderNode)nodes[i]).getVariable(this);

        return String.format("%s = vec4(%s, %s, %s, %s);", (Object[]) args);
    }

    @Override
    public boolean singleUseFragment() {
        return false;
    }
}
