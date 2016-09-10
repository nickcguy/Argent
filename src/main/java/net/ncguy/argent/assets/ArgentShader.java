package net.ncguy.argent.assets;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import net.ncguy.argent.utils.AppUtils;
import net.ncguy.argent.vpl.compiler.IShaderNode;

import java.util.Map;

/**
 * Created by Guy on 05/09/2016.
 */
public class ArgentShader extends BaseShader {

    private ArgShader shader;
    private Renderable renderable;
    Map<Uniform, Setter> map;

    public ArgentShader(ArgShader shader, Renderable renderable) {
        this.shader = shader;
        this.renderable = renderable;
        this.program = shader.shaderProgram();
        register(DefaultShader.Inputs.worldTrans, DefaultShader.Setters.worldTrans);
        register(DefaultShader.Inputs.projViewTrans, DefaultShader.Setters.projViewTrans);
        register(DefaultShader.Inputs.cameraNearFar, DefaultShader.Setters.cameraNearFar);
        register(new Uniform("u_time"), new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, AppUtils.GL.getTime());
            }
        });
        register();
    }

    public void register() {
        if(map != null) map.clear();
        map = AppUtils.Shader.detectUniforms(shader.vertexShaderSource, shader.fragmentShaderSource);
        map.forEach(this::registerUniform);
        System.out.println();
    }

    public void registerUniform(Uniform uniform, Setter setter) {
        System.out.printf("Registering uniform with alias: %s\n", uniform.alias);
        register(uniform, setter);
    }

    public void bind() {
        // Bind Textures
        shader.graph.nodes.stream()
                .filter(node -> node instanceof IShaderNode)
                .map(node -> (IShaderNode)node)
                .forEach(this::bind);
    }
    public void bind(IShaderNode node) {
        node.bind(this.program);
    }

    @Override
    public void begin(Camera camera, RenderContext context) {
        super.begin(camera, context);
        context.setDepthTest(GL30.GL_LEQUAL);
        context.setCullFace(GL30.GL_BACK);
        bind();
    }

    @Override
    public void end() {
        super.end();
    }

    @Override
    public void render(Renderable renderable) {
        if (!renderable.material.has(BlendingAttribute.Type)) {
            context.setBlending(false, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        }else{
            context.setBlending(true, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        }
        super.render(renderable);
    }

    @Override
    public void render(Renderable renderable, Attributes combinedAttributes) {
        super.render(renderable, combinedAttributes);
    }

    @Override
    public void init() {
        if(program != null && shader != null) {
            final ShaderProgram program = this.program;
            this.program = null;
            init(program, renderable);
            renderable = null;
        }
    }

    @Override
    public int compareTo(Shader shader) {
        return 0;
    }

    @Override
    public boolean canRender(Renderable renderable) {
        return program != null && shader != null;
    }


}

