package net.ncguy.argent.render.shader;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import net.ncguy.argent.Argent;
import net.ncguy.argent.parser.GLError;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.List;
import java.util.Map;

/**
 * Created by Guy on 23/06/2016.
 */
public class DynamicShader extends BaseShader {
    public Renderable renderable;

    private static GLError.Parser errorParser;

    public final String vertexShaderCode;
    public final String fragmentShaderCode;
    public final DynamicShader.Info info;

    public DynamicShader(final Renderable renderable, DynamicShader.Info info) {
        this(renderable, info.compile(), info.vertex, info.fragment, info);
    }

    public DynamicShader(final Renderable renderable, final ShaderProgram shaderProgramModelBorder, String vertexShaderCode, String fragmentShaderCode, DynamicShader.Info info)  {
        this.renderable = renderable;
        this.program = shaderProgramModelBorder;
        this.vertexShaderCode = vertexShaderCode;
        this.fragmentShaderCode = fragmentShaderCode;
        this.info = info;

        detectUniforms().forEach(this::register);
    }

    private Map<Uniform, Setter> detectUniforms() {
        return ShaderUtils.detectUniforms(vertexShaderCode, fragmentShaderCode);
    }

    // STANDARD SHADER STUFF

    @Override
    public void end() { super.end(); }

    @Override
    public void begin(final Camera camera, final RenderContext context)  {
        super.begin(camera, context);
        context.setDepthTest(GL20.GL_LEQUAL);
        context.setCullFace(GL20.GL_BACK);
    }

    @Override
    public void render(final Renderable renderable)  {
        if (!renderable.material.has(BlendingAttribute.Type))  {
            context.setBlending(false, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        }else{
            context.setBlending(true, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        }
        super.render(renderable);
    }

    @Override
    public void init() {
        final ShaderProgram program = this.program;
        this.program = null;
        init(program, renderable);
        renderable = null;
    }

    @Override public int compareTo(final Shader other) { return 0; }
    @Override public boolean canRender(final Renderable instance) { return true; }
    @Override public void render(final Renderable renderable, final Attributes combinedAttributes) {
        renderable.meshPart.primitiveType = info.primitive.id;
        try {

            super.render(renderable, combinedAttributes);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class Info {
        public String name;
        public String desc;
        public String vertex;
        public String fragment;
        public GLPrimitiveType primitive = GLPrimitiveType.GL_TRIANGLES;

        public transient List<String> uniforms;
        public transient DefaultMutableTreeNode treeNode;
        private transient final boolean canCompile;

        public final boolean canCompile() { return canCompile; }

        public Info() {
            this(true);
        }

        public Info(boolean canCompile) {
            this.canCompile = canCompile;
        }

        public Info copy() { return copy(false); }
        public Info copy(boolean transitive) {
            Info info = new Info();
            info.name = this.name;
            info.desc = this.desc;
            info.vertex = this.vertex;
            info.fragment = this.fragment;
            info.primitive = this.primitive;
            if(transitive) {
                info.uniforms = this.uniforms;
                info.treeNode = this.treeNode;
            }
            return info;
        }

        @Override
        public String toString() {
            return name.replace(" ", "");
        }

        public ShaderProgram compile() {
            if(!canCompile()) return null;
            ShaderProgram program = new ShaderProgram(vertex, fragment);
            if(program.isCompiled())
                return program;
            Argent.toast(this.name, program.getLog(), errorParser(), 5);
            return null;
        }

    }

    public static GLError.Parser errorParser() {
        if (errorParser == null)
            errorParser = new GLError.Parser();
        return errorParser;
    }

    public enum GLPrimitiveType {
        GL_POINTS(GL30.GL_POINTS),
        GL_LINES(GL30.GL_LINES),
        GL_LINE_STRIP(GL30.GL_LINE_STRIP),
        GL_LINE_LOOP(GL30.GL_LINE_LOOP),
        GL_TRIANGLES(GL30.GL_TRIANGLES),
        GL_TRIANGLE_STRIP(GL30.GL_TRIANGLE_STRIP),
        GL_TRIANGLE_FAN(GL30.GL_TRIANGLE_FAN),
        ;
        GLPrimitiveType(int id) { this.id = id; }
        public final int id;
    }

}
