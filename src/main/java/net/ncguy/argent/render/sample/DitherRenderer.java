package net.ncguy.argent.render.sample;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import net.ncguy.argent.render.BufferRenderer;
import net.ncguy.argent.render.WorldRenderer;
import net.ncguy.argent.render.shader.SimpleTextureShader;

import static net.ncguy.argent.render.WorldRenderer.setupShader;

/**
 * Created by Guy on 14/06/2016.
 */
public class DitherRenderer<T> extends BufferRenderer<T> {

    Texture mask;

    public DitherRenderer(WorldRenderer<T> renderer, Texture mask) {
        super(renderer);
        this.mask = mask;
        this.mask.bind(13);
        this.shaderProgram.begin();
        this.shaderProgram.setUniformi("u_ditherMask", 13);
        this.shaderProgram.end();
    }

    @Override
    public void init() {
        ShaderProgram shaderProgram;
        ModelBatch modelBatch;

        shaderProgram = setupShader("dither");
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

    @Override
    public void setSceneUniforms(ShaderProgram program, int[] mutableId) {
        final int texNum = ++mutableId[0];
        getBufferContents().bind(texNum);
        program.setUniformi(uniformName(), texNum);
    }

    @Override
    public void renderToFBO(float delta) {
        fbo().begin();
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        renderIntoBatch();
        fbo().end();
    }

    @Override
    public String name() {
        return "dither";
    }
}
