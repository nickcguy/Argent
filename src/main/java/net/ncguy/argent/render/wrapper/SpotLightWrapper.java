package net.ncguy.argent.render.wrapper;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.environment.SpotLight;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * Created by Guy on 13/06/2016.
 */
public class SpotLightWrapper extends LightWrapper<SpotLight> {

    public SpotLightWrapper(SpotLight light) {
        super(light);
    }

    @Override
    public void applyToShader(ShaderProgram shader) {
        super.applyToShader(shader);
        shader.setUniformf("u_light.cutoffAngle", light.cutoffAngle);
        shader.setUniformf("u_light.exponent", light.exponent);
        final int textureNum = 9;
        fbo.getColorBufferTexture().bind(textureNum);
        shader.setUniformi("u_type", 1);
        shader.setUniformi("u_depthMapDir", textureNum);
    }

    @Override
    public Camera camera() {
        if(lightMapper == null) {
            lightMapper = new PerspectiveCamera(90, DEPTHMAPSIZE, DEPTHMAPSIZE);
            lightMapper.near = 0.1f;
        }
        lightMapper.far = light.intensity;
        lightMapper.position.set(light.position);
        lightMapper.direction.set(light.direction);
        lightMapper.update();
        return lightMapper;
    }

    @Override
    public void buildFBO() {

    }

}
