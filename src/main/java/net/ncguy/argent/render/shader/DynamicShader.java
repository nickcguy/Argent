package net.ncguy.argent.render.shader;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import java.util.Map;

/**
 * Created by Guy on 23/06/2016.
 */
public class DynamicShader extends BaseShader {
    public Renderable renderable;

    public final String vertexShaderCode;
    public final String fragmentShaderCode;

    public DynamicShader(final Renderable renderable, final ShaderProgram shaderProgramModelBorder, String vertexShaderCode, String fragmentShaderCode)  {
        this.renderable = renderable;
        this.program = shaderProgramModelBorder;
        this.vertexShaderCode = vertexShaderCode;
        this.fragmentShaderCode = fragmentShaderCode;

        detectUniforms().forEach(this::register);
//        register(DefaultShader.Inputs.worldTrans, DefaultShader.Setters.worldTrans);
//        register(DefaultShader.Inputs.projViewTrans, DefaultShader.Setters.projViewTrans);
//        register(DefaultShader.Inputs.normalMatrix, DefaultShader.Setters.normalMatrix);
//        register(DefaultShader.Inputs.cameraPosition, DefaultShader.Setters.cameraPosition);
//        register(DefaultShader.Inputs.cameraNearFar, DefaultShader.Setters.cameraNearFar);
    }

    public Map<Uniform, Setter> detectUniforms() {
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
    @Override public void render(final Renderable renderable, final Attributes combinedAttributes) { super.render(renderable, combinedAttributes); }
}
