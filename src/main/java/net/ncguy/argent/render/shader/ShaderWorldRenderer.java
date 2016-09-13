package net.ncguy.argent.render.shader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import net.ncguy.argent.assets.DynamicShader;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.render.BasicWorldRenderer;
import net.ncguy.argent.utils.AppUtils;
import net.ncguy.argent.utils.MultiTargetFrameBuffer;
import net.ncguy.argent.utils.ScreenshotUtils;
import net.ncguy.argent.world.GameWorld;

/**
 * Created by Guy on 11/09/2016.
 */
public class ShaderWorldRenderer<T extends WorldEntity> extends BasicWorldRenderer<T> {

    ShaderProgram programQuad;
    ShaderProgram programTrace;
    MultiTargetFrameBuffer fbo;
    ModelBatch quadBatch;
    ModelBatch traceBatch;

    public ShaderWorldRenderer(GameWorld<T> world) {
        super(world);
        programQuad = AppUtils.Shader.compileShader(Gdx.files.internal("assets/render/simple/ray.vert"), Gdx.files.internal("assets/render/simple/ray.frag"));
        programTrace = AppUtils.Shader.compileShader(Gdx.files.internal("assets/render/simple/ray.vert"), Gdx.files.internal("assets/render/simple/ray_trace.frag"));

        quadBatch = new ModelBatch(new DefaultShaderProvider() {
            @Override
            protected Shader createShader(Renderable renderable) {
                return new DynamicShader(programQuad, renderable);
            }
        });
        traceBatch = new ModelBatch(new DefaultShaderProvider() {
            @Override
            protected Shader createShader(Renderable renderable) {
                return new DynamicShader(programTrace, renderable);
            }
        });
        fbo = MultiTargetFrameBuffer.create(MultiTargetFrameBuffer.Format.RGBA32F, 1, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true, false);
    }

    @Override
    public void render(ModelBatch batch, float delta) {
        prepare();
        renderTrace(delta);
        apply();
        renderQuad(delta);
    }

    private void bindVector3(ShaderProgram shader, String uniform, Vector3 vec) {
        float[] val = new float[] { vec.x, vec.y, vec.z };
        shader.setUniform3fv(uniform, val, 0, val.length);
    }

    private void prepare() {
        programTrace.begin();
        bindVector3(programTrace, "eye", camera().position);
        bindVector3(programTrace, "ray00", camera().getPickRay(-1, -1).direction);
        bindVector3(programTrace, "ray01", camera().getPickRay(-1,  1).direction);
        bindVector3(programTrace, "ray10", camera().getPickRay( 1, -1).direction);
        bindVector3(programTrace, "ray11", camera().getPickRay( 1,  1).direction);
        programTrace.end();
//        bindVector3(eyeUniform, worldCamera.position);
//        bindVector3(ray00Uniform, worldCamera.getPickRay(-1, -1).direction);
//        bindVector3(ray01Uniform, worldCamera.getPickRay(-1,  1).direction);
//        bindVector3(ray10Uniform, worldCamera.getPickRay( 1, -1).direction);
//        bindVector3(ray11Uniform, worldCamera.getPickRay( 1,  1).direction);
    }

    private void renderTrace(float delta) {
        fbo.begin();
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        super.render(traceBatch, delta);
        if(Gdx.input.isKeyJustPressed(Input.Keys.F2))
            ScreenshotUtils.saveScreenshot(fbo.getWidth(), fbo.getHeight(), "FBO");
        fbo.end();
    }

    private void apply() {
        programQuad.begin();
        fbo.getColorBufferTexture(0).bind(1);
        programQuad.setUniformi("tex", 1);
        programQuad.end();
    }

    private void renderQuad(float delta) {
//        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
//        Gdx.gl.glClearColor(0, 0, 0, 1);
        super.render(quadBatch, delta);
    }

    @Override
    public void dispose() {

    }
}
