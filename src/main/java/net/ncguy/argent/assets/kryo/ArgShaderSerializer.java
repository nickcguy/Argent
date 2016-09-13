package net.ncguy.argent.assets.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import net.ncguy.argent.assets.ArgShader;
import net.ncguy.argent.vpl.VPLGraph;

/**
 * Created by Guy on 31/08/2016.
 */
public class ArgShaderSerializer extends Serializer<ArgShader> {

    @Override
    public void write(Kryo kryo, Output output, ArgShader object) {
        kryo.writeObject(output, object.name());
        kryo.writeObject(output, object.vertexShaderSource);
        kryo.writeObject(output, object.fragmentShaderSource);

        // Variables
//        int size = object.variables.size();
//        kryo.writeObject(output, size);
//        object.variables.forEach(var -> kryo.writeObject(output, var));

        // Graph
        kryo.writeObject(output, object.graph);
    }

    @Override
    public ArgShader read(Kryo kryo, Input input, Class<ArgShader> type) {
        String name = kryo.readObject(input, String.class);
        String vert = kryo.readObject(input, String.class);
        String frag = kryo.readObject(input, String.class);

        VPLGraph graph = kryo.readObject(input, VPLGraph.class);

        ArgShader shader = new ArgShader(vert, frag, graph);
        graph.refreshMenu("shader");

//        int varCount = kryo.readObject(input, int.class);
//        for (int i = 0; i < varCount; i++)
//            shader.variables.add(kryo.readObject(input, AbstractArgShaderVariable.class));


        shader.name(name);
        return shader;
    }
}
