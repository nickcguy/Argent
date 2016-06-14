package net.ncguy.argent.render.wrapper;

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
        shader.setUniformf("u_light.position", light.position);
        shader.setUniformf("u_light.direction", light.direction);
        shader.setUniformf("u_light.intensity", light.intensity);
        shader.setUniformf("u_light.cutoffAngle", light.cutoffAngle);
        shader.setUniformf("u_light.exponent", light.exponent);
    }

}
