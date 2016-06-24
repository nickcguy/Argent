package net.ncguy.argent.render.renderer;

import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import net.ncguy.argent.render.BufferRenderer;
import net.ncguy.argent.render.WorldRenderer;
import net.ncguy.argent.render.shader.DynamicShader;

/**
 * Created by Guy on 23/06/2016.
 */
public class DynamicRenderer<T> extends BufferRenderer<T> {

    private DynamicShader.Info info;

    public DynamicRenderer(WorldRenderer<T> renderer, DynamicShader.Info info) {
        super(renderer, false);
        this.info = info;
        init();
    }

    @Override
    public void init() {
        ShaderProgram shaderProgram;
        ModelBatch modelBatch;

        shaderProgram = info.compile();
        modelBatch = new ModelBatch(new DefaultShaderProvider(){
            @Override
            protected Shader createShader(Renderable renderable) {
                return new DynamicShader(renderable, shaderProgram, info.vertex, info.fragment);
            }
        });
        this.modelBatch = modelBatch;
        this.shaderProgram = shaderProgram;
    }

    @Override
    public void render(float delta, boolean toScreen) {
        super.render(delta, toScreen);
    }

    @Override
    public void setSceneUniforms(ShaderProgram program, int[] mutableId) {
        final int texNum = mutableId[0]++;
        getBufferContents().bind(texNum);
        String uniformName = uniformName();
        program.setUniformi(uniformName, texNum);
    }

    @Override
    public String name() {
        return info.name;
    }

    @Override
    public String uniformName() {
        return "u_"+name().replace(" ", "");
    }

    @Override
    public void invalidateFBO() {
        if(fbo == null) return;
        fbo.dispose();
        fbo = null;
    }
}

