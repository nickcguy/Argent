package net.ncguy.argent.vpl.compiler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import net.ncguy.argent.injector.ArgentInjector;
import net.ncguy.argent.injector.Inject;
import net.ncguy.argent.ui.Toaster;
import net.ncguy.argent.utils.AppUtils;
import net.ncguy.argent.vpl.VPLGraph;
import net.ncguy.argent.vpl.VPLManager;
import net.ncguy.argent.vpl.VPLNode;
import net.ncguy.argent.vpl.annotations.ShaderNodeData;
import net.ncguy.argent.vpl.nodes.widget.TextureNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Guy on 22/08/2016.
 */
public class ShaderProgramCompiler extends VPLCompiler<ShaderProgram> {

    public String vertexShader;

    @Inject
    Toaster toaster;

    public ShaderProgramCompiler() {
        ArgentInjector.inject(this);
        vertexShader = Gdx.files.internal("assets/shaders/graph/dynamic.vert").readString();
    }

    public void prepare(VPLGraph graph) {
        final int[] index = {1};
        graph.nodes.stream().filter(n -> n instanceof TextureNode).map(n -> (TextureNode)n).forEach(n -> {
            n.texUnit = index[0]++;
        });
    }

    @Override
    public ShaderProgram compile(VPLGraph graph, VPLNode root) {

        List<VPLNode<?>> validate = validate(graph, root);
        if(validate != null) {
            toast(Toaster.ToastType.ERROR, "Invalid nodes detected");
            validate.forEach(n -> toast(Toaster.ToastType.ERROR, VPLManager.instance().getDisplayName(n)));
            return null;
        }

        prepare(graph);

        Map<VPLNode, AbstractMap.SimpleEntry<String, String>> fragmentMap = new HashMap<>();

        graph.nodes.stream().filter(node -> node instanceof IShaderNode).forEach(node -> fragmentMap.put(node, getShaderFragment((IShaderNode) node)));

        StringBuilder fragmentShader = new StringBuilder();
        fragmentShader.append("#version 450\n\n");

        fragmentShader.append("layout (location = 0) out vec4 texNormal;\n")
                .append("layout (location = 1) out vec4 texDiffuse;\n")
                .append("layout (location = 2) out vec4 texSpcAmbDis;\n")
                .append("layout (location = 3) out vec4 texEmissive;\n")
                .append("layout (location = 4) out vec4 texReflection;\n")
                .append("layout (location = 5) out vec4 texPosition;\n")
                .append("layout (location = 6) out vec4 texModNormal;\n\n");

        fragmentMap.values().forEach(e -> fragmentShader.append(e.getKey()).append("\n"));

        StringBuilder fragmentShaderBody = new StringBuilder();

        // Diffuse
        fragmentShaderBody.append("\n");
        ShaderNodeData.Packet diffuse = getNodePacker(root, 0);
        if(diffuse != null) {
            fragmentShaderBody.append("\t").append(diffuse.fragment).append("\n");
            fragmentShaderBody.append("\t").append("texDiffuse = ").append(diffuse.variable).append("\n");
        }else{
            fragmentShaderBody.append("\t").append("texDiffuse = vec4(1.0);\n");
        }

        // Normal
        fragmentShaderBody.append("\n");
        ShaderNodeData.Packet normal = getNodePacker(root, 1);
        if (normal != null) {
            fragmentShaderBody.append("\t").append(normal.fragment).append("\n");
            fragmentShaderBody.append("\t").append("texNormal = ").append(normal.variable).append("\n");
        }else{
            fragmentShaderBody.append("\t").append("texNormal = vec4(1.0);\n");
        }

        // spcAmbDis

        // Specular
        fragmentShaderBody.append("\n");
        ShaderNodeData.Packet spec = getNodePacker(root, 2);
        if(spec != null) {
            fragmentShaderBody.append("\t").append(spec.fragment).append("\n");
            fragmentShaderBody.append("\t").append("float internal_specular = ").append(spec.variable).append("\n");
        }else{
            fragmentShaderBody.append("\t").append("float internal_specular = 0.0;\n");
        }
        // Ambient
        ShaderNodeData.Packet amb = getNodePacker(root, 3);
        if(amb != null) {
            fragmentShaderBody.append("\t").append(amb.fragment).append("\n");
            fragmentShaderBody.append("\t").append("float internal_ambient = ").append(amb.variable).append("\n");
        }else{
            fragmentShaderBody.append("\t").append("float internal_ambient = 0.0;\n");
        }
        // Displacement
        ShaderNodeData.Packet dis = getNodePacker(root, 4);
        if(dis != null) {
            fragmentShaderBody.append("\t").append(dis.fragment).append("\n");
            fragmentShaderBody.append("\t").append("float internal_displacement = ").append(dis.variable).append("\n");
        }else{
            fragmentShaderBody.append("\t").append("float internal_displacement = 0.0;\n");
        }
        fragmentShaderBody.append("\n");
        fragmentShaderBody.append("\t").append("texSpcAmbDis = vec4(internal_specular, internal_ambient, internal_displacement, 1.0);\n");

        // Emissive
        fragmentShaderBody.append("\n");
        ShaderNodeData.Packet emi = getNodePacker(root, 5);
        if(emi != null) {
            fragmentShaderBody.append("\t").append(emi.fragment).append("\n");
            fragmentShaderBody.append("\t").append("texEmissive = ").append(emi.variable).append("\n");
        }else{
            fragmentShaderBody.append("\t").append("texEmissive = vec4(vec3(0.0), 1.0);\n");
        }

        // Reflection
        fragmentShaderBody.append("\n");
        ShaderNodeData.Packet ref = getNodePacker(root, 6);
        if(ref != null) {
            fragmentShaderBody.append("\t").append(ref.fragment).append("\n");
            fragmentShaderBody.append("\t").append("texReflection = ").append(ref.variable).append("\n");
        }else{
            fragmentShaderBody.append("\t").append("texReflection = vec4(vec3(0.0), 1.0);\n");
        }

//        fragmentMap.values().forEach(e -> {
//            String uniforms = e.getKey();
//            String fragment = e.getValue();
//            fragmentShader.append(uniforms).append("\n");
//            fragmentShaderBody.append("\t").append(fragment).append("\n");
//        });

        String shader = fragmentShader.toString();

        shader += "void main() { \n";
        shader += fragmentShaderBody.toString();
        shader += "}\n";

        try {
            debug(shader);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return AppUtils.Shader.compileShader(this.vertexShader, shader);
    }

    public List<VPLNode<?>> validate(VPLGraph graph, VPLNode<?> root) {
        List<VPLNode<?>> list = graph.getNetworkedNodes(root).stream().filter(node -> !VPLManager.instance().isShaderNode(node)).collect(Collectors.toList());
        if(list.size() > 0)
            return list;
        return null;
    }

    public ShaderNodeData.Packet getNodePacker(VPLNode root, int index) {
        VPLNode node = root.getInputNodeAtPin(index);
        if(node == null) return null;
        if(node == root) return null;
        return VPLManager.instance().getShaderNodeData(node);
    }

    private void debug(String shader) throws IOException {
        File debugFile = new File("dynamicShader.frag");
        Path path = debugFile.toPath();
        Files.deleteIfExists(path);

        Files.write(path, shader.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
    }

    public AbstractMap.SimpleEntry<String, String> getShaderFragment(IShaderNode node) {
        return new AbstractMap.SimpleEntry<>(node.getUniforms(), node.getFragment());
    }

    private void toast(Toaster.ToastType type, String toast) {
        System.out.printf("[%s] %s\n", type.name(), toast);
        if(toaster != null)
            toaster.toast(type, 3, toast);
    }

}
