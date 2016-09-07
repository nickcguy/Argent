package net.ncguy.argent.vpl.nodes.widget;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.kotcrab.vis.ui.widget.spinner.SimpleFloatSpinnerModel;
import com.kotcrab.vis.ui.widget.spinner.Spinner;
import net.ncguy.argent.vpl.VPLGraph;
import net.ncguy.argent.vpl.VPLNode;
import net.ncguy.argent.vpl.annotations.NodeData;
import net.ncguy.argent.vpl.compiler.IShaderNode;
import net.ncguy.argent.vpl.nodes.WidgetNode;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static net.ncguy.argent.vpl.VPLPin.Types.COMPOUND;
import static net.ncguy.argent.vpl.VPLPin.Types.OUTPUT;

/**
 * Created by Guy on 05/09/2016.
 */
@NodeData(value = "Float", execIn = false, execOut = false, tags = {"shader","maths"}, outputTypes = float.class)
public class FloatNode extends WidgetNode<Float> implements IShaderNode {

    Spinner spinner;
    SimpleFloatSpinnerModel model;

    public FloatNode(VPLGraph graph) {
        super(graph);
    }

    public FloatNode(VPLGraph graph, Method method) {
        super(graph, method);
    }

    @Override
    protected void buildInput() {
        model = new SimpleFloatSpinnerModel(0, -1024, 1024, .01f, 2);
        spinner = new Spinner("", model);
        addElement(inputTable, spinner).colspan(2).width(130);
    }

    @Override
    protected void buildOutput() {
        addPin(outputTable, float.class, "", OUTPUT, COMPOUND);
    }

    @Override
    protected void discernType() {
        discernType(float.class, 1);
    }

    @Override
    public Float fetchData(VPLNode node) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        return Float.valueOf(spinner.getModel().getText());
    }

    @Override
    public void writeToFile(Kryo kryo, Output output) {
        kryo.writeObject(output, model.getValue());
    }

    @Override
    public void readFromFile(Kryo kryo, Input input) {
        model.setValue(kryo.readObject(input, float.class));
    }

    // Shader Stuff


    @Override
    public void resetStaticCache() {
        globalIndex = 0;
    }

    @Override
    public void resetCache() {
        index = globalIndex++;
        fragmentUsed = false;
    }

    static int globalIndex;
    int index;
    boolean fragmentUsed;

    @Override
    public String getUniforms() {
        return "";
    }

    @Override
    public String getVariable(int pinId) {
        return spinner.getModel().getText();
    }

    @Override
    public String getFragment() {
        return "";
    }
}
