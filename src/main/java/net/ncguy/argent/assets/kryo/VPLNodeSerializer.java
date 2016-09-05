package net.ncguy.argent.assets.kryo;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.SerializationException;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import net.ncguy.argent.utils.ReflectionUtils;
import net.ncguy.argent.vpl.VPLGraph;
import net.ncguy.argent.vpl.VPLNode;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Guy on 31/08/2016.
 */
public class VPLNodeSerializer<T extends VPLNode> extends Serializer<T> {

    public static VPLGraph graph;

    @Override
    public void write(Kryo kryo, Output output, VPLNode object) {
        kryo.writeObject(output, object.getClass().getCanonicalName());
        kryo.writeObject(output, object.continuous);
        kryo.writeObject(output, object.position);
        String methodRef = "";
        if(object.method != null)
            methodRef = object.method.toString();
        kryo.writeObject(output, methodRef);

        object.writeToFile(kryo, output);
    }

    @Override
    public T read(Kryo kryo, Input input, Class<T> type) {
        if(graph == null)
            throw new SerializationException("VPLGraph is not set, cannot deserialize VPLNodes");
        String className = kryo.readObject(input, String.class);
        boolean continuous = kryo.readObject(input, boolean.class);
        Vector2 position = kryo.readObject(input, Vector2.class);
        String methodRef = kryo.readObject(input, String.class);
        Method method = getMethod(methodRef);

        Class<? extends VPLNode> cls = null;
        try {
            cls = (Class<? extends VPLNode>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        VPLNode<?> node = null;
        try {
            node = cls.getConstructor(VPLGraph.class, Method.class).newInstance(graph, method);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
        node.continuous = continuous;
        node.position = position;

        node.readFromFile(kryo, input);

        return (T) node;
    }

    public Method getMethod(String ref) {
        if(ref.replace(" ", "").length() <= 0) return null;

        String[] split = ref.split(" ");
        String sig = split[split.length-1];
        int cut = sig.indexOf("(");
        String[] params = sig.substring(cut).replace("(", "").replace(")", "").split(",");
        sig = sig.substring(0, cut);
        System.out.println(sig);

        int splitIndex = sig.lastIndexOf(".");
        String clsStr = sig.substring(0, splitIndex);
        String name = sig.substring(splitIndex+1);

        Class<?> cls;
        try {
            cls = Class.forName(clsStr);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        Class<?>[] paramCls = new Class[params.length];
        for (int i = 0; i < params.length; i++) try {
            paramCls[i] = ReflectionUtils.forName(params[i]);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            Method method = cls.getDeclaredMethod(name, paramCls);
            return method;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

}
