package net.ncguy.argent.render.wrapper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.glutils.FrameBufferCubemap;
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import net.ncguy.argent.utils.ScreenshotFactory;

import java.util.List;

/**
 * Created by Guy on 13/06/2016.
 */
public class PointLightWrapper extends LightWrapper<PointLight> {


    public PointLightWrapper(PointLight light) {
        super(light);
    }

    @Override
    public void applyToShader(ShaderProgram shader) {
        super.applyToShader(shader);
        final int textureNum = 10;
        if(fbo == null) buildFBO();
        fbo.getColorBufferTexture().bind(textureNum);
        shader.setUniformi("u_type", 2);
        shader.setUniformi("u_depthMapCube", textureNum);
    }

    @Override
    public Camera camera() {
        if(lightMapper == null) {
            lightMapper = new PerspectiveCamera(90, DEPTHMAPSIZE, DEPTHMAPSIZE);
            lightMapper.near = 0.1f;
        }
        lightMapper.far = light.intensity*10;
        lightMapper.position.set(light.position);
        lightMapper.update();
        return lightMapper;
    }

    @Override
    public void buildFBO() {
        fbo = new FrameBufferCubemap(Pixmap.Format.RGBA8888, DEPTHMAPSIZE, DEPTHMAPSIZE, true);
    }

    @Override
    public GLFrameBuffer draw(List<ModelInstance> renderables) {
        applyToShader(shaderProgram);
        if(fbo == null) buildFBO();
        fbo.begin();
        while(((FrameBufferCubemap)fbo).nextSide()) {
            camera().direction.set(
                    ((FrameBufferCubemap) fbo)
                            .getSide()
                            .direction);
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
            batch.begin(camera());
            batch.render(renderables);
            batch.end();
            if (Gdx.input.isKeyJustPressed(Input.Keys.F3))
                ScreenshotFactory.saveScreenshot(DEPTHMAPSIZE, DEPTHMAPSIZE, "PointLight");
        }
        fbo.end();
        return fbo;
    }
}
