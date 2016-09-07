package net.ncguy.argent.vpl.nodes.shader;

import com.badlogic.gdx.graphics.Color;
import net.ncguy.argent.vpl.VPLGraph;
import net.ncguy.argent.vpl.annotations.NodeData;

import java.lang.reflect.Method;

import static net.ncguy.argent.vpl.VPLPin.Types.COMPOUND;
import static net.ncguy.argent.vpl.VPLPin.Types.OUTPUT;

/**
 * Created by Guy on 07/09/2016.
 */
@NodeData(value = "Frag Coordinate", tags = "shader")
public class FragCoordinateNode extends VPLShaderNode<Color> {

    public FragCoordinateNode(VPLGraph graph) {
        super(graph);
    }

    public FragCoordinateNode(VPLGraph graph, Method method) {
        super(graph, method);
    }

    @Override
    protected void buildInput() {

    }

    @Override
    protected void buildOutput() {
        addPin(outputTable, Color.class, "Frag Coordinate", OUTPUT, COMPOUND);
    }

    @Override
    protected void discernType() {
        discernType(Color.class, 0);
    }

    @Override
    public String getUniforms() {
        return "";
    }

    @Override
    public String getVariable(int pinId) {
        return "Position";
    }

    @Override
    public String getFragment() {
        return "";
    }
}
