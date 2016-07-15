package net.ncguy.argent.render.wrapper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import net.ncguy.argent.render.WorldRenderer;
import net.ncguy.argent.render.shader.DepthMapShader;
import net.ncguy.argent.utils.ScreenshotFactory;

import java.util.List;

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
public abstract class LightWrapper<T extends BaseLight<T>> implements Disposable {

    public static final int DEPTHMAPSIZE = 1024;

    protected GLFrameBuffer fbo;

    public T light;
    protected ShaderProgram shaderProgram;
    protected ModelBatch batch;
    protected Camera lightMapper;
    protected ModelInstance shadowVolume;

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
        shader.setUniformf("u_lightPosition", camera().position);
        shader.setUniformf("u_cameraFar", camera().far);
    }

    public abstract Camera camera();
    public abstract void buildFBO();

    public GLFrameBuffer draw(List<ModelInstance> renderables) {
        applyToShader(shaderProgram);
        if(fbo == null) buildFBO();
        fbo.begin();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        batch.begin(camera());
        batch.render(renderables);
        batch.end();
        if(Gdx.input.isKeyJustPressed(Input.Keys.F3))
            ScreenshotFactory.saveScreenshot(DEPTHMAPSIZE, DEPTHMAPSIZE, light.getClass().getSimpleName());
        fbo.end();
        return fbo;
    }

    @Override
    public void dispose() {
        if(fbo != null) {
            fbo.dispose();
            fbo = null;
        }
        if(light != null) {
            light = null;
        }
        if(shaderProgram != null) {
            shaderProgram.dispose();
            shaderProgram = null;
        }
        if(batch != null) {
            batch.dispose();
            batch = null;
        }
        if(lightMapper != null) {
            lightMapper = null;
        }
    }

    public void invalidate() {
        if(fbo != null) {
            fbo.dispose();
            fbo = null;
        }
    }
}
