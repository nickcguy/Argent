package net.ncguy.argent.vpl;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import net.ncguy.argent.utils.AppUtils;
import net.ncguy.argent.vpl.nodes.BasicNodeFunctions;
import net.ncguy.argent.vpl.struct.ColourDescriptor;
import net.ncguy.argent.vpl.struct.Vector2Descriptor;
import net.ncguy.argent.vpl.struct.Vector3Descriptor;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Guy on 18/08/2016.
 */
public class VPLManager {

    private static VPLManager instance;
    public static VPLManager instance() {
        if (instance == null)
            instance = new VPLManager();
        return instance;
    }

    private VPLManager() {
        registeredNodes = new ArrayList<>();
        structDescriptors = new HashMap<>();
        registerDefaultDescriptors();
        registerDefaultNodes();
    }

    private void registerDefaultDescriptors() {
        registerStruct(Vector2.class, new Vector2Descriptor());
        registerStruct(Vector3.class, new Vector3Descriptor());
        registerStruct(Color.class, new ColourDescriptor());
    }

    private void registerDefaultNodes() {
        final List<Method> methods = new ArrayList<>();
        Collections.addAll(methods, BasicNodeFunctions.class.getDeclaredMethods());
        structDescriptors.forEach((cls, desc) -> Collections.addAll(methods, desc.getClass().getDeclaredMethods()));

        methods.stream()
                .filter(method -> method.isAnnotationPresent(NodeData.class))
                .forEach(this::registerNode);
    }

    public Color getPinColour(VPLPin pin) {
        return getPinColour(pin.cls);
    }
    public Color getPinColour(Class<?> type) {
        if(structDescriptors.containsKey(type))
            return structDescriptors.get(type).colour();
        return Color.WHITE;
    }

    List<Method> registeredNodes;
    Map<Class, IStructDescriptor> structDescriptors;

    public <T> void registerStruct(Class<T> cls, IStructDescriptor<T, ?> desc) {
        structDescriptors.put(cls, desc);
    }

    public void registerNode(Method method) {
        registeredNodes.add(method);
    }

    public List<Method> getNodesWithTags(final String... tags) {
        if(tags.length == 0) return registeredNodes;
        return registeredNodes.stream().filter(n -> {
            for (String tag : tags)
                if(isTaggedWith(n, tag)) return true;
            return false;
        }).collect(Collectors.toList());
    }

    public boolean isTaggedWith(Method method, String tag) {
        if(!method.isAnnotationPresent(NodeData.class)) return false;
        NodeData data = method.getAnnotation(NodeData.class);
        return AppUtils.General.arrayContains(data.tags(), tag);
    }

    public String getDisplayName(Method method) {
        if(!method.isAnnotationPresent(NodeData.class)) return method.getName();
        NodeData data = method.getAnnotation(NodeData.class);
        return data.value();
    }

    public String getCategory(Method method) {
        if(!method.isAnnotationPresent(NodeData.class)) return "";
        NodeData data = method.getAnnotation(NodeData.class);
        return data.category();
    }

    public String[] getTags(Method method) {
        if(!method.isAnnotationPresent(NodeData.class)) return new String[]{"*"};
        NodeData data = method.getAnnotation(NodeData.class);
        return data.tags();
    }

    public String[] getKeywords(Method method) {
        if(!method.isAnnotationPresent(NodeData.class)) return new String[]{""};
        NodeData data = method.getAnnotation(NodeData.class);
        return data.keywords();
    }

    public boolean canExecIn(Method method) {
        if(!method.isAnnotationPresent(NodeData.class)) return true;
        NodeData data = method.getAnnotation(NodeData.class);
        return data.execIn();
    }
    public boolean canExecOut(Method method) {
        if(!method.isAnnotationPresent(NodeData.class)) return true;
        NodeData data = method.getAnnotation(NodeData.class);
        return data.execOut();
    }

}
