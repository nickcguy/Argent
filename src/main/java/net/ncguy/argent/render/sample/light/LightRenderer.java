package net.ncguy.argent.render.sample.light;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Quaternion;
import net.ncguy.argent.render.BufferRenderer;
import net.ncguy.argent.render.WorldRenderer;
import net.ncguy.argent.render.shader.DepthMapShader;
import net.ncguy.argent.utils.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guy on 10/07/2016.
 */
public class LightRenderer<T> extends BufferRenderer<T> {

    List<Quaternion> lightPoints;
    private String rawVertShader;
    private String rawFragShader;

    public LightRenderer(WorldRenderer<T> renderer) {
        super(renderer);
        try { getRawShader(); } catch (Exception e) { e.printStackTrace(); }
    }

    private void getRawShader() throws Exception {
        rawFragShader = Gdx.files.internal("shaders/light/light.frag").readString();
        rawVertShader = Gdx.files.internal("shaders/light/light.vert").readString();
    }

    @Override
    public void init() {
        lightPoints = new ArrayList<>();
        addLight(new Quaternion(100, 100, 100, 1000));
    }

    public void compileShader() {
        try { getRawShader(); } catch (Exception e) { e.printStackTrace(); }
        String frag = rawFragShader;
        String vert = rawVertShader;

        vert = TextUtils.replaceLine("#version", "", vert);

        vert = "#version 120\n" +
                "\n" +
                "#define LIGHT_COUNT "+lightPoints.size()+"\n" +
                "\n" +
                vert;

        shaderProgram = new ShaderProgram(vert, frag);

        if(!shaderProgram.isCompiled()) {
            System.out.println(shaderProgram.getLog());
            System.out.println();
            System.out.println("Vertex");
            System.out.println(shaderProgram.getVertexShaderSource());
            System.out.println();
            System.out.println("Fragment");
            System.out.println(shaderProgram.getFragmentShaderSource());
            System.out.println();
        }

        for(int i = 0; i < lightPoints.size(); i++){
            Quaternion quat = lightPoints.get(i);
            int loc = shaderProgram.getUniformLocation("u_lightPos[" + i + "]");
            float[] pack = new float[]{quat.x, quat.y, quat.z, quat.w};
            shaderProgram.setUniform4fv(loc, pack, 0, pack.length);
        }

        modelBatch = new ModelBatch(new DefaultShaderProvider(){
            @Override
            protected Shader createShader(Renderable renderable) {
                return new DepthMapShader(renderable, shaderProgram);
            }
        });
    }

    public void addLight(Quaternion lightPos) {
        lightPoints.add(lightPos);
        compileShader();
    }

    @Override
    public String name() {
        return "Light";
    }
}
