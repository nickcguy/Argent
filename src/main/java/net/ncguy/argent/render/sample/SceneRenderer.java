package net.ncguy.argent.render.sample;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import net.ncguy.argent.render.BufferRenderer;
import net.ncguy.argent.render.WorldRenderer;
import net.ncguy.argent.render.shader.SimpleTextureShader;

import static net.ncguy.argent.render.WorldRenderer.setupShader;

/**
 * Created by Guy on 13/06/2016.
 */
public class SceneRenderer<T> extends BufferRenderer<T> {

    Environment env;

    public SceneRenderer(WorldRenderer<T> renderer) {
        super(renderer);
        env = new Environment();
        env.add(new DirectionalLight().set(Color.RED.mul(0.1f), Vector3.X));
    }

    @Override
    public void init() {
        ShaderProgram shaderProgram;
        ModelBatch modelBatch;

        shaderProgram = setupShader("scene");
        modelBatch = new ModelBatch(new DefaultShaderProvider(){
            @Override
            protected Shader createShader(Renderable renderable) {
                return new SimpleTextureShader(renderable, shaderProgram);
//                renderable.environment = env;
//                return new DefaultShader(renderable, new DefaultShader.Config());
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

    }

    @Override
    public String name() {
        return "scene";
    }
}
