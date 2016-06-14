package net.ncguy.argent.render.sample;

import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import net.ncguy.argent.render.BufferRenderer;
import net.ncguy.argent.render.WorldRenderer;
import net.ncguy.argent.render.shader.NormalTextureShader;

import static net.ncguy.argent.render.WorldRenderer.setupShader;

/**
 * Created by Guy on 14/06/2016.
 */
public class NormalRenderer<T> extends BufferRenderer<T> {

    public NormalRenderer(WorldRenderer<T> renderer) {
        super(renderer);
    }

    @Override
    public void init() {
        ShaderProgram shaderProgram;
        ModelBatch modelBatch;

        shaderProgram = setupShader("normal");
        modelBatch = new ModelBatch(new DefaultShaderProvider(){
            @Override
            protected Shader createShader(Renderable renderable) {
                return new NormalTextureShader(renderable, shaderProgram);
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
    public void setSceneUniforms(ShaderProgram program, int[] mutableId) {
        final int texNum = ++mutableId[0];
        getBufferContents().bind(texNum);
        program.setUniformi(uniformName(), texNum);
    }

    @Override
    public void renderIntoBatch() {
        super.renderIntoBatch();
    }

    @Override
    public String name() {
        return "normal";
    }
}
