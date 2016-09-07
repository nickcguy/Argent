package net.ncguy.argent.vpl;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import net.ncguy.argent.utils.AppUtils;
import net.ncguy.argent.vpl.annotations.NodeColour;
import net.ncguy.argent.vpl.annotations.NodeData;
import net.ncguy.argent.vpl.compiler.IShaderNode;
import net.ncguy.argent.vpl.nodes.BasicNodeFunctions;
import net.ncguy.argent.vpl.nodes.FunctionalNodes;
import net.ncguy.argent.vpl.nodes.factory.NodeFactory;
import net.ncguy.argent.vpl.nodes.shader.FinalShaderNode;
import net.ncguy.argent.vpl.struct.*;
import net.ncguy.argent.vpl.struct.bridge.IStructBridge;
import net.ncguy.argent.vpl.struct.bridge.QuaternionColourBridge;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
        registeredNodeFactories = new ArrayList<>();
        structDescriptors = new HashMap<>();
        structBridges = new HashMap<>();
        registerDefaultDescriptors();
        registerDefaultBridges();
        registerDefaultNodes();
        SearchAndRegisterFactories();
    }

    private void registerDefaultDescriptors() {
        registerStruct(Vector2.class, new Vector2Descriptor());
        registerStruct(Vector3.class, new Vector3Descriptor());
        registerStruct(Color.class, new ColourDescriptor());
        registerStruct(Quaternion.class, new QuaternionDescriptor());
        registerStruct(String.class, new StringDescriptor());

        final List<Method> methods = new ArrayList<>();
        structDescriptors.forEach((cls, desc) -> Collections.addAll(methods, desc.getClass().getDeclaredMethods()));
        methods.stream()
                .filter(this::validMethod)
                .forEach(this::registerNode);
    }

    private void registerDefaultBridges() {
        registerBridge(Quaternion.class, Color.class, new QuaternionColourBridge());
    }

    private void registerDefaultNodes() {
        registerNodeClass(BasicNodeFunctions.class);
        registerNodeClass(FinalShaderNode.class);
        registerNodeClass(FunctionalNodes.class);
    }

    private void SearchAndRegisterFactories() {
        Reflections ref = new Reflections();
        VPLModule.getNodeFactoryLocations().forEach(s -> {
            ref.merge(new Reflections(s));
        });
        ref.getTypesAnnotatedWith(NodeData.class)
                .stream()
                .filter(VPLNode.class::isAssignableFrom)
                .map(cls -> (Class<? extends VPLNode>)cls)
                .forEach(cls -> registeredNodeFactories.add(new NodeFactory(cls)));

//        this.menu.addItem(new SearchableList.Item<>(null, "Texture Node", new NodeFactory(TextureNode.class)));
//        this.menu.addItem(new SearchableList.Item<>(null, "Shader Node", new NodeFactory(FinalShaderNode.class)));
//        this.menu.addItem(new SearchableList.Item<>(null, "TexCoords Node", new NodeFactory(TextureCoordinatesNode.class)));
//        this.menu.addItem(new SearchableList.Item<>(null, "Passthrough", new NodeFactory(VariablePassthroughNode.class)));
//        this.menu.addItem(new SearchableList.Item<>(null, "Make Colour", new NodeFactory(MakeColourNode.class)));
//        this.menu.addItem(new SearchableList.Item<>(null, "Break Colour", new NodeFactory(BreakColourNode.class)));
//        this.menu.addItem(new SearchableList.Item<>(null, "1-", new NodeFactory(OneMinusNode.class)));
//        this.menu.addItem(new SearchableList.Item<>(null, "Float", new NodeFactory(FloatNode.class)));
//        this.menu.addItem(new SearchableList.Item<>(null, "Panner", new NodeFactory(PannerNode.class)));
//        this.menu.addItem(new SearchableList.Item<>(null, "Cartesian To Polar", new NodeFactory(ToPolarNode.class)));
//        this.menu.addItem(new SearchableList.Item<>(null, "Polar To Cartesian", new NodeFactory(ToCartesian.class)));
//        this.menu.addItem(new SearchableList.Item<>(null, "Rotate Vector2", new NodeFactory(RotateVector2.class)));
//
//        this.menu.addItem(new SearchableList.Item<>(null, "Break Vector2", new NodeFactory(BreakVector2.class)));
//        this.menu.addItem(new SearchableList.Item<>(null, "Break Vector3", new NodeFactory(BreakVector3.class)));
//        this.menu.addItem(new SearchableList.Item<>(null, "Break Colour", new NodeFactory(BreakColour.class)));
//        this.menu.addItem(new SearchableList.Item<>(null, "Make Vector2", new NodeFactory(MakeVector2.class)));
//        this.menu.addItem(new SearchableList.Item<>(null, "Make Vector3", new NodeFactory(MakeVector3.class)));
//        this.menu.addItem(new SearchableList.Item<>(null, "Make Colour", new NodeFactory(MakeColour.class)));
    }

    public boolean validMethod(Method method) {
        if(!method.isAnnotationPresent(NodeData.class)) return printErrorMessage(method, "No node data present");
        if(method.getParameterTypes().length < 1) return printErrorMessage(method, "No parameters");
        if(method.getParameterTypes().length < 2) return printErrorMessage(method, "Insufficient parameters");
        if(!method.getParameterTypes()[0].equals(VPLNode.class)) return printErrorMessage(method, "No node parameter in first slot");
        if(!method.getParameterTypes()[1].equals(Integer.TYPE)) return printErrorMessage(method, "No pinId parameter in first slot");
        if(!Modifier.isStatic(method.getModifiers())) return printErrorMessage(method, "Must be static");
        return true;
    }

    private boolean printErrorMessage(Method method, String message) {
        System.out.printf("%s >> %s\n", method.toString(), message);
        return false;
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
    List<NodeFactory> registeredNodeFactories;
    Map<Class, IStructDescriptor> structDescriptors;
    Map<AbstractMap.SimpleEntry<Class, Class>, IStructBridge> structBridges;

    public <T> void registerStruct(Class<T> cls, IStructDescriptor desc) {
        structDescriptors.put(cls, desc);
    }

    public <T, U> void registerBridge(Class<T> clsA, Class<U> clsB, IStructBridge<T, U> bridge) {
        structBridges.put(new AbstractMap.SimpleEntry<>(clsA, clsB), bridge);
    }

    public void registerNode(Method method) {
        registeredNodes.add(method);
    }
    public void registerNodeClass(Class clazz) {
        final List<Method> methods = new ArrayList<>();
        Collections.addAll(methods, clazz.getDeclaredMethods());
        methods.stream()
                .filter(this::validMethod)
                .forEach(this::registerNode);
    }

    public List<Method> getNodesWithTags(final String... tags) {
        if(tags.length == 0) return registeredNodes;
        if(AppUtils.General.arrayContains(tags, "*")) return registeredNodes;
        return registeredNodes.stream().filter(n -> {
            for (String tag : tags)
                if(isTaggedWith(n, tag)) return true;
            return false;
        }).collect(Collectors.toList());
    }

    public <T, U> boolean canBridgeType(Class<T> cls, Class<U> target) {
        if(structBridges.containsKey(new AbstractMap.SimpleEntry<>(cls, target)))
            return true;
        AbstractMap.SimpleEntry<Class<U>, Class<T>> entry = new AbstractMap.SimpleEntry<>(target, cls);
        if(structBridges.containsKey(entry))
            if(structBridges.get(entry).biDirectional()) return true;
        return false;
    }

    public String[] getTags(Class<?> cls) {
        if(cls.isAnnotationPresent(NodeData.class)) {
            NodeData data = cls.getAnnotation(NodeData.class);
            return data.tags();
        }
        return new String[]{"*"};
    }

    public String[] getKeywords(Class<?> cls) {
        if(cls.isAnnotationPresent(NodeData.class)) {
            NodeData data = cls.getAnnotation(NodeData.class);
            return data.keywords();
        }
        return new String[]{""};
    }

    public String[] getMatchable(Class<?> cls) {
        String[] keywords = getKeywords(cls);
        String[] tags = getTags(cls);
        return AppUtils.General.union(keywords, tags);
    }

    public boolean isTaggedWith(Method method, String tag) {
        if(!method.isAnnotationPresent(NodeData.class)) return false;
        NodeData data = method.getAnnotation(NodeData.class);
        if(AppUtils.General.arrayContains(data.tags(), "*")) return true;
        return AppUtils.General.arrayContains(data.tags(), tag);
    }

    public String getDisplayName(VPLNode node) {
        if(node.method != null) return getDisplayName(node.method);
        if(node.getClass().isAnnotationPresent(NodeData.class)) {
            NodeData data = node.getClass().getAnnotation(NodeData.class);
            return data.value();
        }
        return node.getClass().getSimpleName();
    }

    public String getCategory(VPLNode node) {
        if(node.method != null) return getCategory(node.method);
        if(node.getClass().isAnnotationPresent(NodeData.class)) {
            NodeData data = node.getClass().getAnnotation(NodeData.class);
            return data.category();
        }
        return "";
    }

    public String[] getTags(VPLNode node) {
        if(node.method != null) return getTags(node.method);
        if(node.getClass().isAnnotationPresent(NodeData.class)) {
            NodeData data = node.getClass().getAnnotation(NodeData.class);
            return data.tags();
        }
        return new String[]{"*"};
    }

    public String[] getKeywords(VPLNode node) {
        if(node.method != null) return getKeywords(node.method);
        if(node.getClass().isAnnotationPresent(NodeData.class)) {
            NodeData data = node.getClass().getAnnotation(NodeData.class);
            return data.keywords();
        }
        return new String[]{""};
    }

    public String[] getMatchable(VPLNode node) {
        String[] keywords = getKeywords(node);
        String[] tags = getTags(node);
        return AppUtils.General.union(keywords, tags);
    }

    public boolean canExecIn(VPLNode node) {
        if(node.method != null) return canExecIn(node.method);
        if(node.getClass().isAnnotationPresent(NodeData.class)) {
            NodeData data = node.getClass().getAnnotation(NodeData.class);
            return data.execIn();
        }
        return true;
    }
    public boolean canExecOut(VPLNode node) {
        if(node.method != null) return canExecOut(node.method);
        if(node.getClass().isAnnotationPresent(NodeData.class)) {
            NodeData data = node.getClass().getAnnotation(NodeData.class);
            return data.execOut();
        }
        return true;
    }

    public int getOutPins(VPLNode node) {
        if(node.method != null) return getOutPins(node.method);
        if(node.getClass().isAnnotationPresent(NodeData.class)) {
            NodeData data = node.getClass().getAnnotation(NodeData.class);
            return data.outPins();
        }
        return 1;
    }

    public NodeColour getNodeColourData(VPLNode node) {
        if(node.method != null) return getNodeColourData(node.method);
        if(node.getClass().isAnnotationPresent(NodeData.class)) {
            NodeData data = node.getClass().getAnnotation(NodeData.class);
            return data.colour();
        }
        return null;
    }

    public Class<?>[] getOutputTypes(VPLNode node) {
        if(node.method != null) return getOutputTypes(node.method);
        if(node.getClass().isAnnotationPresent(NodeData.class)) {
            NodeData data = node.getClass().getAnnotation(NodeData.class);
            return data.outputTypes();
        }
        return new Class[]{Object.class};
    }
    public Class<?> getOutputTypeOfPin(int pinId, VPLNode node) {
        Class<?>[] types = getOutputTypes(node);
        return types[MathUtils.clamp(pinId, 0, types.length - 1)];
    }

    public String[] getArgNames(VPLNode node) {
        if(node.method != null) return getArgNames(node.method);
        if(node.getClass().isAnnotationPresent(NodeData.class)) {
            NodeData data = node.getClass().getAnnotation(NodeData.class);
            return data.argNames();
        }
        return new String[0];
    }

    public NodeData getNodeData(VPLNode node) {
        if(node.method != null) return getNodeData(node.method);
        if(node.getClass().isAnnotationPresent(NodeData.class))
            return node.getClass().getAnnotation(NodeData.class);
        return null;
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

    public String[] getMatchable(Method method) {
        String[] keywords = getKeywords(method);
        String[] tags = getTags(method);
        return AppUtils.General.union(keywords, tags);
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

    public Color getColour(Class<?> cls) {
        if(structDescriptors.containsKey(cls))
            return structDescriptors.get(cls).colour();
        return Color.WHITE;
    }

    public int getOutPins(Method method) {
        if(!method.isAnnotationPresent(NodeData.class)) return 1;
        NodeData data = method.getAnnotation(NodeData.class);
        return data.outPins();
    }

    public NodeColour getNodeColourData(Method method) {
        if(!method.isAnnotationPresent(NodeData.class)) return null;
        NodeData data = method.getAnnotation(NodeData.class);
        return data.colour();
    }

    public Color getNodeColour(Method method) {
        NodeColour c = getNodeColourData(method);
        if(c == null) return new Color(1, 1, 1, 1);
        return getNodeColour(c);
    }

    public Color getNodeColour(NodeColour c) {
        return new Color(c.r(), c.g(), c.b(), c.a());
    }

    public Class<?>[] getOutputTypes(Method method) {
        if(!method.isAnnotationPresent(NodeData.class)) return new Class[]{Object.class};
        NodeData data = method.getAnnotation(NodeData.class);
        return data.outputTypes();
    }

    public Class<?> getOutputTypeOfPin(int pinId, Method method) {
        Class<?>[] types = getOutputTypes(method);
        return types[MathUtils.clamp(pinId, 0, types.length - 1)];
    }

    public String[] getArgNames(Method method) {
        if(!method.isAnnotationPresent(NodeData.class)) return new String[0];
        NodeData data = method.getAnnotation(NodeData.class);
        return data.argNames();
    }

    public NodeData getNodeData(Method method) {
        if(!method.isAnnotationPresent(NodeData.class)) return null;
        return method.getAnnotation(NodeData.class);
    }

    public boolean isShaderNode(VPLNode<?> node) {
        return node instanceof IShaderNode;
    }

}
