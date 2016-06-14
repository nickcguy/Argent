package net.ncguy.argent.render.wrapper;

import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;
import net.ncguy.argent.render.WorldRenderer;
import net.ncguy.argent.render.shader.DepthMapShader;

/**
 * Make sure that, when using lighting, your shader has the correct light struct, detailed below:
 *

 struct Light {
    vec4 colour;
    vec3 direction;
    vec3 position;
    float intensity;
    float cutoffAngle;
    float exponent;
 };

 uniform Light u_light;

 *
 */
public abstract class LightWrapper<T extends BaseLight<T>> {

    public T light;
    private ShaderProgram shaderProgram;
    private ModelBatch batch;

    public LightWrapper(T light) {
        this.light = light;
        shaderProgram = WorldRenderer.setupShader("depth/light");
        if(!shaderProgram.isCompiled())
            throw new GdxRuntimeException("Unable to compile shader: "+shaderProgram.getLog());
        batch = new ModelBatch(new DefaultShaderProvider() {
            @Override
            protected Shader createShader(Renderable renderable) {
                return new DepthMapShader(renderable, shaderProgram);
            }
        });
    }

    public void applyToShader(ShaderProgram shader) {
        shader.setUniformf("u_light.colour", light.color);
    }



}
