package net.ncguy.argent.render.sample;

import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import net.ncguy.argent.render.BufferRenderer;
import net.ncguy.argent.render.WorldRenderer;

/**
 * Created by Guy on 14/06/2016.
 */
public class UberRenderer<T> extends BufferRenderer<T> {

    public UberRenderer(WorldRenderer<T> renderer) {
        super(renderer);
    }

    @Override
    public void init() {
        ShaderProgram shaderProgram = null;
        ModelBatch modelBatch = null;

//        shaderProgram = setupShader("uber");
        modelBatch = new ModelBatch(new DefaultShaderProvider(){
            @Override
            protected Shader createShader(Renderable renderable) {
                return new DefaultShader(renderable, new DefaultShader.Config());
            }
        });
//        if(!shaderProgram.isCompiled()) {
//            System.out.println(shaderProgram.getLog());
//            return;
//        }
        this.shaderProgram = shaderProgram;
        this.modelBatch = modelBatch;
    }

    @Override
    public void setSceneUniforms(ShaderProgram program, int[] mutableId) {
        super.setSceneUniforms(program, mutableId);
    }

    @Override
    public String name() {
        return "UberShader";
    }
}
