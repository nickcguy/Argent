package net.ncguy.argent.render.shader;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader.Inputs;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader.Setters;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import net.ncguy.argent.render.wrapper.LightWrapper;

import java.util.List;

/**
 * Created by Guy on 13/06/2016.
 */
public class ShadowMapShader extends BaseShader {

    public Renderable renderable;
    public List<LightWrapper> lights;

    @Override
    public void end()
    {
        super.end();
    }

    public ShadowMapShader(List<LightWrapper> lights, final Renderable renderable, final ShaderProgram shaderProgramModelBorder)  {
        this.lights = lights;
        this.renderable = renderable;
        this.program = shaderProgramModelBorder;
        register(Inputs.worldTrans, Setters.worldTrans);
        register(Inputs.projViewTrans, Setters.projViewTrans);
        register(Inputs.normalMatrix, Setters.normalMatrix);
        register(Inputs.diffuseTexture, Setters.diffuseTexture);
        register(Inputs.diffuseColor, Setters.diffuseColor);

        register(Inputs.cameraPosition, Setters.cameraPosition);
        register(new Uniform("u_lightTrans"), new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, shader.camera.combined);
            }
        });
    }

    @Override
    public void begin(final Camera camera, final RenderContext context)
    {
        super.begin(camera, context);
        context.setDepthTest(GL20.GL_LEQUAL);
        context.setCullFace(GL20.GL_BACK);

    }

    @Override
    public void render(final Renderable renderable)
    {
        if (!renderable.material.has(BlendingAttribute.Type))
        {
            context.setBlending(false, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        }
        else
        {
            context.setBlending(true, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        }
        super.render(renderable);
    }

    @Override
    public void init()
    {
        final ShaderProgram program = this.program;
        this.program = null;
        init(program, renderable);
        renderable = null;
    }

    @Override
    public int compareTo(final Shader other)
    {
        return 0;
    }

    @Override
    public boolean canRender(final Renderable instance)
    {
        return true;
    }

    @Override
    public void render(final Renderable renderable, final Attributes combinedAttributes)  {
        boolean firstCall = true;
        for(final LightWrapper light : lights) {
            light.applyToShader(program);
            if(firstCall){
                context.setDepthTest(GL20.GL_LEQUAL);
                context.setBlending(false, GL20.GL_ONE, GL20.GL_ONE);
                super.render(renderable, combinedAttributes);
                firstCall = false;
            }else{
                context.setDepthTest(GL20.GL_EQUAL);
                context.setBlending(true, GL20.GL_ONE, GL20.GL_ONE);
                renderable.meshPart.render(program, false);
            }
        }
    }

}
