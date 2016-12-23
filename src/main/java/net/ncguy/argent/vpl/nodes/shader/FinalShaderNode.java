package net.ncguy.argent.vpl.nodes.shader;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import net.ncguy.argent.vpl.VPLGraph;
import net.ncguy.argent.vpl.VPLManager;
import net.ncguy.argent.vpl.VPLNode;
import net.ncguy.argent.vpl.annotations.NodeData;
import net.ncguy.argent.vpl.compiler.IShaderNode;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static net.ncguy.argent.vpl.VPLPin.Types.INPUT;

/**
 * Created by Guy on 22/08/2016.
 */
@NodeData(value = "Shader attributes", execIn = false, execOut = false, outPins = 0, tags = "shader",
        argNames = {
                "Diffuse Colour",
                "Normal",
                "Specular",
                "Ambient",
                "Metallic",
                "Roughness",
                "Emissive",
                "World Position"
        },
        outputTypes = {
            Color.class,
            Color.class,
            float.class,
            float.class,
            float.class,
            float.class,
            Color.class,
            Vector3.class
        }
)
public class FinalShaderNode extends VPLNode<Object> implements IShaderNode {

    public FinalShaderNode(VPLGraph graph, Method method) {
        this(graph);
    }

    public FinalShaderNode(VPLGraph graph) {
        super(graph, null);
    }

    @Override
    public void invokeSelf(int pin) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        return;
//        if(graph.compiler == null)
//            graph.compiler = new ShaderProgramCompiler();
//        VPLCompiler compiler = graph.compiler;
//        if(compiler instanceof ShaderProgramCompiler)
//            compiler.compile(graph, this);
    }

    @Override
    public Object fetchData(VPLNode node) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        return null;
    }

    @Override
    protected void buildInput() {
        String[] argNames = VPLManager.instance().getArgNames(this);
        Class<?>[] argTypes = VPLManager.instance().getOutputTypes(this);
        for (int i = 0; i < argNames.length; i++)
            addPin(inputTable, argTypes[i], argNames[i], INPUT);

    }

    @Override
    protected void discernType() {
        discernType(Object.class, 7);
    }

    @Override
    protected void buildOutput() {
    }

    @Override
    public String getUniforms() {
        return "";
    }

    @Override
    public String getVariable(int pinId) {
        return "";
    }

    @Override
    public String getFragment() {
        return "";
    }
}
