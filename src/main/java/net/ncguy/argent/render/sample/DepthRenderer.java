package net.ncguy.argent.render.sample;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import net.ncguy.argent.render.BufferRenderer;
import net.ncguy.argent.render.WorldRenderer;
import net.ncguy.argent.render.shader.SimpleTextureShader;
import net.ncguy.argent.render.wrapper.PointLightWrapper;
import net.ncguy.argent.utils.ScreenshotFactory;

import static net.ncguy.argent.render.WorldRenderer.setupShader;

/**
 * Created by Guy on 13/06/2016.
 */
public class DepthRenderer<T> extends BufferRenderer<T> {

    public DepthRenderer(WorldRenderer<T> renderer) {
        super(renderer);
    }

    @Override
    public void init() {
        ShaderProgram shaderProgram;
        ModelBatch modelBatch;

        shaderProgram = setupShader("depth");
        modelBatch = new ModelBatch(new DefaultShaderProvider(){
            @Override
            protected Shader createShader(Renderable renderable) {
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

    public void setLight() {
        PointLightWrapper l = new PointLightWrapper(new PointLight());
        l.light.position.set(10, 10, 10);
        l.light.intensity = 10;
        l.applyToShader(shaderProgram);
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
}
