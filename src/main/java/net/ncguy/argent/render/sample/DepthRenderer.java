package net.ncguy.argent.render.sample;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import net.ncguy.argent.editor.swing.shader.internal.simpledepth.SimpleDepthDescriptor;
import net.ncguy.argent.render.BufferRenderer;
import net.ncguy.argent.render.WorldRenderer;
import net.ncguy.argent.render.shader.SimpleTextureShader;
import net.ncguy.argent.render.wrapper.LightWrapper;
import net.ncguy.argent.render.wrapper.PointLightWrapper;
import net.ncguy.argent.utils.ScreenshotFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guy on 13/06/2016.
 */
public class DepthRenderer<T> extends BufferRenderer<T> {

    private static SimpleDepthDescriptor descriptor = new SimpleDepthDescriptor();
    List<LightWrapper> lights;

    public DepthRenderer(WorldRenderer<T> renderer) {
        super(renderer);
        this.lights = new ArrayList<>();
        addLight(new PointLightWrapper(new PointLight().set(Color.WHITE, new Vector3(-300, 300, -300), 500)));
    }

    public void addLight(LightWrapper light) {
        this.lights.add(light);
    }

    public void removeLight(LightWrapper light) {
        this.lights.remove(light);
        light.dispose();
    }

    @Override
    public void init() {
        ShaderProgram shaderProgram;
        ModelBatch modelBatch;

        shaderProgram = descriptor.compile();
        modelBatch = new ModelBatch(new DefaultShaderProvider(){
            @Override
            protected Shader createShader(Renderable renderable) {
//                return new ShadowMapShader(lights, renderable, shaderProgram);
                return new SimpleTextureShader(renderable, shaderProgram);
            }
        });
        if(!shaderProgram.isCompiled()) {
            System.out.println(shaderProgram.getLog());
            return;
        }
        this.shaderProgram = shaderProgram;
        this.modelBatch = modelBatch;
    }

    @Override
    public void render(float delta, boolean toScreen) {
        lights.forEach(l -> l.draw(renderer.renderables()));
        super.render(delta, toScreen);
    }

    @Override
    public void renderIntoBatch() {
        shaderProgram.begin();
        shaderProgram.setUniformf("u_cameraFar", renderer.camera().far);
        shaderProgram.setUniformf("u_lightPos", renderer.camera().position);
        shaderProgram.end();
        super.renderIntoBatch();
        if(Gdx.input.isKeyJustPressed(Input.Keys.F2))
            ScreenshotFactory.saveScreenshot(fbo.getWidth(), fbo.getHeight(), "depth");
    }

    @Override
    public void setSceneUniforms(ShaderProgram program, int[] mutableId) {
        final int texNum = ++mutableId[0];
        getBufferContents().bind(texNum);
        program.setUniformi(uniformName(), texNum);
    }

    @Override
    public String name() {
        return "depth";
    }

    @Override
    public void invalidateFBO() {
        if(fbo == null) return;
        fbo.dispose();
        fbo = null;
        lights.forEach(LightWrapper::invalidate);
    }
}
