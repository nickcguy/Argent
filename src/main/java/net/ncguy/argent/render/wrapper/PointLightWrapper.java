package net.ncguy.argent.render.wrapper;

import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

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
        shader.setUniformf("u_light.position", light.position);
        shader.setUniformf("u_light.intensity", light.intensity);
    }

}
