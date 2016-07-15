package net.ncguy.argent.render.shader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import net.ncguy.argent.render.sample.light.volumetric.VolumetricLight;

import java.util.List;

/**
 * Created by Guy on 12/07/2016.
 */
public class VolumetricDepthShader extends BaseShader {

    public Renderable renderable;
    protected List<VolumetricLight> lights;

    @Override
    public void end() {
        super.end();
    }

    public VolumetricDepthShader(final Renderable renderable, final ShaderProgram shaderProgramModelBorder, List<VolumetricLight> lights) {
        this.renderable = renderable;
        this.program = shaderProgramModelBorder;
        this.lights = lights;

        register(DefaultShader.Inputs.worldTrans, DefaultShader.Setters.worldTrans);
        register(DefaultShader.Inputs.projViewTrans, DefaultShader.Setters.projViewTrans);
        register(DefaultShader.Inputs.normalMatrix, DefaultShader.Setters.normalMatrix);
    }

    @Override
    public void begin(final Camera camera, final RenderContext context) {
        super.begin(camera, context);
        context.setDepthTest(GL20.GL_LEQUAL);
        context.setCullFace(GL20.GL_BACK);
    }

    @Override
    public void render(final Renderable renderable) {
        if (!renderable.material.has(BlendingAttribute.Type)) {
            context.setBlending(false, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        } else {
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

    @Override
    public int compareTo(final Shader other) {
        return 0;
    }

    @Override
    public boolean canRender(final Renderable instance) {
        return true;
    }

    @Override
    public void render(final Renderable renderable, final Attributes combinedAttributes) {
        renderable.meshPart.primitiveType = GL20.GL_TRIANGLES;
        final boolean[] firstCall = {true};
        lights.forEach(l -> {
            l.applyToShader(program);
            if(Gdx.input.isKeyJustPressed(Input.Keys.L))
            for (String s : program.getUniforms()) {
                System.out.println(s);
            }
            if(true) {
                context.setDepthTest(GL20.GL_LEQUAL);
                context.setBlending(false, GL20.GL_ONE, GL20.GL_ONE);
                super.render(renderable, combinedAttributes);
                firstCall[0] = false;
            }else{
                context.setDepthTest(GL20.GL_EQUAL);
                context.setBlending(true, GL20.GL_ONE, GL20.GL_ONE);
                MeshPart part = renderable.meshPart;
                part.mesh.render(program, part.primitiveType, part.offset, part.size, false);
            }
        });
    }
}