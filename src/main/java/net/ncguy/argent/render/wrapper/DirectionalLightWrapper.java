package net.ncguy.argent.render.wrapper;

import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
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
        shader.setUniformf("u_light.direction", light.direction);
    }
}
