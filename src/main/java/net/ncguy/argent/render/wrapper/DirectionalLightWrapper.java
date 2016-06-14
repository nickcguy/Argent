package net.ncguy.argent.render.wrapper;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * Created by Guy on 13/06/2016.
 */
public class DirectionalLightWrapper extends LightWrapper<DirectionalLight> {

    public DirectionalLightWrapper(DirectionalLight light) {
        super(light);
    }

    @Override
    public void applyToShader(ShaderProgram shader) {
        super.applyToShader(shader);
        final int textureNum = 9;
        fbo.getColorBufferTexture().bind(textureNum);
        shader.setUniformi("u_type", 1);
        shader.setUniformi("u_depthMapDir", textureNum);
        shader.setUniformMatrix("u_lightTrans", camera().combined);
    }

    @Override
    public Camera camera() {
        if(lightMapper == null)
            lightMapper = new OrthographicCamera(DEPTHMAPSIZE, DEPTHMAPSIZE);
        lightMapper.position.set(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
        lightMapper.direction.set(light.direction);
        lightMapper.update();
        return lightMapper;
    }

    @Override
    public void buildFBO() {
        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, DEPTHMAPSIZE, DEPTHMAPSIZE, true);
    }
}
