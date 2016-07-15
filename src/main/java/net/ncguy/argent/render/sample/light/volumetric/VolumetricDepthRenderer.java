package net.ncguy.argent.render.sample.light.volumetric;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import net.ncguy.argent.render.BufferRenderer;
import net.ncguy.argent.render.WorldRenderer;
import net.ncguy.argent.render.shader.DepthMapShader;
import net.ncguy.argent.render.shader.VolumetricDepthShader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guy on 12/07/2016.
 */
public class VolumetricDepthRenderer<T> extends BufferRenderer<T> {

    List<VolumetricLight> lightPoints;
    ShaderProgram lightShaderProgram;
    ModelBatch lightModelBatch;

    public VolumetricDepthRenderer(WorldRenderer<T> renderer) {
        super(renderer);
    }

    public void addLight(VolumetricLight light) {
        lightPoints.add(light);
        compileShader();
    }

    @Override
    public void init() {
        lightPoints = new ArrayList<>();
//        addLight(new SpotLight(renderer, 100, 100, 100, 1000));
        addLight(new SpotLight(renderer, -400, 600, -400, 2500));
        compileShader();
    }

    protected void compileShader() {
        FileHandle lightVert = Gdx.files.internal("shaders/volumetric/light.vert");
        FileHandle lightFrag = Gdx.files.internal("shaders/volumetric/light.frag");

        lightShaderProgram = new ShaderProgram(lightVert, lightFrag);
        lightModelBatch = new ModelBatch(new DefaultShaderProvider(){
            @Override
            protected Shader createShader(Renderable renderable) {
                return new DepthMapShader(renderable, lightShaderProgram);
            }
        });

        FileHandle compVert = Gdx.files.internal("shaders/volumetric/comp.vert");
        FileHandle compFrag = Gdx.files.internal("shaders/volumetric/comp.frag");

        shaderProgram = new ShaderProgram(compVert, compFrag);
        modelBatch = new ModelBatch(new DefaultShaderProvider() {
            @Override
            protected Shader createShader(Renderable renderable) {
                return new VolumetricDepthShader(renderable, shaderProgram, lightPoints);
            }
        });
        System.out.println(shaderProgram.getLog());
    }

    @Override
    public void render(float delta, boolean toScreen) {
        for (VolumetricLight light : lightPoints)
            light.render(lightModelBatch);
        super.render(delta, toScreen);
    }

    @Override
    public String name() {
        return "Volumetric";
    }
}
