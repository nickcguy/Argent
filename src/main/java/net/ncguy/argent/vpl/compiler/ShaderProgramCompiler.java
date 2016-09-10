package net.ncguy.argent.vpl.compiler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import net.ncguy.argent.assets.ArgShader;
import net.ncguy.argent.injector.ArgentInjector;
import net.ncguy.argent.injector.Inject;
import net.ncguy.argent.ui.Toaster;
import net.ncguy.argent.utils.AppUtils;
import net.ncguy.argent.vpl.VPLGraph;
import net.ncguy.argent.vpl.VPLManager;
import net.ncguy.argent.vpl.VPLNode;
import net.ncguy.argent.vpl.nodes.widget.TextureNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Guy on 22/08/2016.
 */
public class ShaderProgramCompiler extends VPLCompiler<ArgShader> {

    public String vertexShader;

    @Inject
    Toaster toaster;

    public ShaderProgramCompiler() {
        ArgentInjector.inject(this);
        vertexShader = Gdx.files.internal("assets/shaders/graph/dynamic.vert").readString();
    }

    public void prepare(VPLGraph graph) {
        TextureNode.globalTexUnit = 1;
        List<IShaderNode> nodes = graph.nodes.stream()
                .filter(node -> node instanceof IShaderNode)
                .map(node -> (IShaderNode) node)
                .collect(Collectors.toList());
        nodes.forEach(IShaderNode::resetStaticCache);
        nodes.forEach(IShaderNode::resetCache);
    }

    @Override
    public ArgShader compile(VPLGraph graph, VPLNode root, ArgShader argShader) {

        List<VPLNode<?>> validate = validate(graph, root);
        if(validate != null) {
            toast(Toaster.ToastType.ERROR, "Invalid nodes detected");
            validate.forEach(n -> toast(Toaster.ToastType.ERROR, VPLManager.instance().getDisplayName(n)));
            return null;
        }

        prepare(graph);

        Map<VPLNode, String> fragmentMap = new HashMap<>();

        graph.nodes.stream().filter(node -> node instanceof IShaderNode).forEach(node -> fragmentMap.put(node, ((IShaderNode)node).getUniforms()));

        StringBuilder fragmentShader = new StringBuilder();
        fragmentShader.append("#version 450\n\n");

        fragmentShader.append("layout (location = 0) out vec4 texNormal;\n")
                .append("layout (location = 1) out vec4 texDiffuse;\n")
                .append("layout (location = 2) out vec4 texSpcAmbDisRef;\n")
                .append("layout (location = 3) out vec4 texEmissive;\n")
                .append("layout (location = 4) out vec4 texPosition;\n")
                .append("layout (location = 5) out vec4 texModNormal;\n\n");

        fragmentShader.append("in vec4 Position;\n");
        fragmentShader.append("in vec3 Normal;\n");

        fragmentMap.values().forEach(e -> append(fragmentShader, e, false));

        StringBuilder fragmentShaderBody = new StringBuilder();

        fragmentShaderBody.append("\t// Single-use fragments, typically used for texture sampling\n");
        graph.nodes.stream()
                .filter(node -> node instanceof IShaderNode)
                .map(node -> (IShaderNode)node)
                .filter(IShaderNode::singleUseFragment)
                .forEach(n -> append(fragmentShaderBody, n.getFragment()));

        // Diffuse
        fragmentShaderBody.append("\n");
        IShaderNode diffuse = getNodePacker(root, 0);
        if(diffuse != null) {
            append(fragmentShaderBody, diffuse.getFragment());
            append(fragmentShaderBody, "texDiffuse = " + diffuse.getVariable(root)+";");
        }else{
            append(fragmentShaderBody, "texDiffuse = vec4(1.0);");
        }

        // Normal
        fragmentShaderBody.append("\n");
        IShaderNode normal = getNodePacker(root, 1);
        if (normal != null) {
            append(fragmentShaderBody, normal.getFragment());
            append(fragmentShaderBody, "texNormal = " + normal.getVariable(root)+";");
        }else{
            append(fragmentShaderBody, "texNormal = vec4(1.0);");
        }

        // spcAmbDis

        // Specular
        fragmentShaderBody.append("\n");
        IShaderNode spec = getNodePacker(root, 2);
        if(spec != null) {
            append(fragmentShaderBody, spec.getFragment());
            append(fragmentShaderBody, "float internal_specular = " + spec.getVariable(root)+";");
        }else{
            append(fragmentShaderBody, "float internal_specular = 0.0;");
        }
        // Ambient
        IShaderNode amb = getNodePacker(root, 3);
        if(amb != null) {
            append(fragmentShaderBody, amb.getFragment());
            append(fragmentShaderBody, "float internal_ambient = "+amb.getVariable(root)+";");
        }else{
            append(fragmentShaderBody, "float internal_ambient = 0.0;");
        }
        // Displacement
        IShaderNode dis = getNodePacker(root, 4);
        if(dis != null) {
            append(fragmentShaderBody, dis.getFragment());
            append(fragmentShaderBody, "float internal_displacement = "+dis.getVariable(root)+";");
//            fragmentShaderBody.append("\t").append(dis.getFragment()).append("\n");
//            fragmentShaderBody.append("\t").append("float internal_displacement = ").append(dis.getVariable(root)).append("\n");
        }else{
            append(fragmentShaderBody, "float internal_displacement = 0.0;");
//            fragmentShaderBody.append("\t").append("float internal_displacement = 0.0;\n");
        }
        // Reflection
        IShaderNode ref = getNodePacker(root, 5);
        if(ref != null) {
            append(fragmentShaderBody, ref.getFragment());
            append(fragmentShaderBody, "float internal_reflection = "+ref.getVariable(root)+";");
        }else{
            append(fragmentShaderBody, "float internal_reflection = 0.0;");
        }
        fragmentShaderBody.append("\n");
        append(fragmentShaderBody, "texSpcAmbDisRef = vec4(internal_specular, internal_ambient, internal_displacement, internal_reflection);");

        // Emissive
        fragmentShaderBody.append("\n");
        IShaderNode emi = getNodePacker(root, 6);
        if(emi != null) {
            append(fragmentShaderBody, emi.getFragment());
            append(fragmentShaderBody, "texEmissive = "+emi.getVariable(root)+";");
        }else{
            append(fragmentShaderBody, "texEmissive = vec4(vec3(0.0), 1.0);");
        }

        // World position
        fragmentShaderBody.append("\n");
        IShaderNode pos = getNodePacker(root, 7);
        if(pos != null) {
            append(fragmentShaderBody, pos.getFragment());
            append(fragmentShaderBody, f("texPosition = vec4(%s, 1.0);", pos.getVariable(root)));
        }else{
            append(fragmentShaderBody, "texPosition = vec4(Position.xyz, 1.0);");
        }

        // Indistinct variable allocation
        append(fragmentShaderBody, "vec3 norm = Normal * texNormal.rgb;");
        append(fragmentShaderBody, "texModNormal = vec4(norm, 1.0);");

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

        ShaderProgram prg = AppUtils.Shader.compileShader(this.vertexShader, shader, true);

        String log = prg.getLog();
        String[] lines = log.split("\n");
        if(lines.length > 0) {
            if(lines[0].replace(" ", "").length() > 0) {
                toaster.error("Shader compiled with errors");
                for (String line : lines) {
                    toaster.error(line);
                }
            }
        }

        if(argShader == null)
            argShader = new ArgShader(prg);

        argShader.vertexShaderSource = vertexShader;
        argShader.fragmentShaderSource = shader;

        return argShader;
    }

    private String f(String format, String... args) {
        return String.format(format, args);
    }

    private void append(StringBuilder sb, String text) {
        append(sb, text, true);
    }
    private void append(StringBuilder sb, String text, boolean indent) {
        if(text.replace(" ", "").length() <= 0) return;
        if(indent) sb.append("\t");
        sb.append(text).append("\n");
    }

    public List<VPLNode<?>> validate(VPLGraph graph, VPLNode<?> root) {
        List<VPLNode<?>> list = graph.getNetworkedNodes(root).stream().filter(node -> !VPLManager.instance().isShaderNode(node)).collect(Collectors.toList());
        if(list.size() > 0)
            return list;
        return null;
    }

    public IShaderNode getNodePacker(VPLNode root, int index) {
        return root.getNodePacker(index);
    }

    private void debug(String shader) throws IOException {
        File debugFile = new File("dynamicShader.frag");
        Path path = debugFile.toPath();
        Files.deleteIfExists(path);

        Files.write(path, shader.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
    }

    private void toast(Toaster.ToastType type, String toast) {
        System.out.printf("[%s] %s\n", type.name(), toast);
        if(toaster != null)
            toaster.toast(type, 3, toast);
    }

}
