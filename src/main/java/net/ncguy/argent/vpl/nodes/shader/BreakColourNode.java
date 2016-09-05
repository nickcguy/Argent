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
@NodeData(value = "Break Colour", execIn = false, execOut = false, tags = "shader", outputTypes = Float.class)
public class BreakColourNode extends VPLNode<Float> implements IShaderNode {

    public BreakColourNode(VPLGraph graph, Method method) {
        this(graph);
    }

    public BreakColourNode(VPLGraph graph) {
        super(graph, null);
    }

    @Override
    protected void discernType() {
        discernType(Color.class, 1);
    }

    @Override
    protected void buildInput() {
        addPin(inputTable, Color.class, "Colour", INPUT);
    }

    @Override
    protected void buildOutput() {
        addPin(outputTable, Float.class, "Red", OUTPUT, COMPOUND).useNativeColour(false).getColour().set(VPLReference.PinColours.RED);
        addPin(outputTable, Float.class, "Green", OUTPUT, COMPOUND).useNativeColour(false).getColour().set(VPLReference.PinColours.GREEN);
        addPin(outputTable, Float.class, "Blue", OUTPUT, COMPOUND).useNativeColour(false).getColour().set(VPLReference.PinColours.BLUE);
        addPin(outputTable, Float.class, "Alpha", OUTPUT, COMPOUND).useNativeColour(false).getColour().set(VPLReference.PinColours.ALPHA);

    }

    // Shader Stuff

    @Override
    public String getUniforms() {
        return "";
    }

    @Override
    public String getVariable(int pinId) {
        String swizzle = "";
        switch(pinId) {
            case 0: swizzle = ".r";     break;
            case 1: swizzle = ".g";     break;
            case 2: swizzle = ".b";     break;
            case 3: swizzle = ".a";     break;
        }
        VPLNode node = getInputNodeAtPin(0);
        if(!(node instanceof IShaderNode))
            return "Error//";
        return ((IShaderNode)node).getVariable(this) + swizzle;
    }

    @Override
    public String getFragment() {
        return "";
    }

}
