package net.ncguy.argent.vpl.nodes.shader;

import com.badlogic.gdx.math.Vector2;
import net.ncguy.argent.vpl.annotations.NodeData;
import net.ncguy.argent.vpl.VPLGraph;
import net.ncguy.argent.vpl.VPLManager;
import net.ncguy.argent.vpl.VPLNode;
import net.ncguy.argent.vpl.compiler.IShaderNode;

import java.lang.reflect.InvocationTargetException;

import static net.ncguy.argent.vpl.VPLPin.Types.COMPOUND;
import static net.ncguy.argent.vpl.VPLPin.Types.OUTPUT;

/**
 * Created by Guy on 29/08/2016.
 */
@NodeData(value = "Texture Coordinates", execIn = false, execOut = false, tags = "shader", argNames = "UV", outputTypes = Vector2.class)
public class TextureCoordinatesNode extends VPLNode<Vector2> implements IShaderNode {

    public TextureCoordinatesNode(VPLGraph graph) {
        super(graph, null);
    }

    @Override
    public void invokeSelf(int pin) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
    }

    @Override
    public Vector2 fetchData(VPLNode node) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        return null;
    }

    @Override protected void buildInput() {}

    @Override
    protected void discernType() {
        discernType(Vector2.class, 0);
    }

    @Override
    protected void buildOutput() {
        String[] argNames = VPLManager.instance().getArgNames(this);
        Class<?>[] argTypes = VPLManager.instance().getOutputTypes(this);
        for (int i = 0; i < argNames.length; i++)
            addPin(outputTable, argTypes[i], argNames[i], OUTPUT, COMPOUND);
    }

    @Override
    public String getUniforms() {
        return "in vec2 TexCoords;";
    }

    @Override
    public String getVariable() {
        return "TexCoords";
    }

    @Override
    public String getFragment() {
        return "";
    }
}
