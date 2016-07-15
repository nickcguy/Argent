package net.ncguy.argent.render.sample.light.volumetric;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import net.ncguy.argent.render.WorldRenderer;

/**
 * Created by Guy on 12/07/2016.
 */
public class SpotLight extends VolumetricLight {

    public Vector3 direction = new Vector3(1, 0, 0);

    public SpotLight(WorldRenderer world) {
        super(world);
    }

    public SpotLight(WorldRenderer world, Quaternion lightData) {
        super(world, lightData);
    }

    public SpotLight(WorldRenderer world, float x, float y, float z, float w) {
        super(world, x, y, z, w);
    }

    @Override
    public void update() {
        camera().direction.set(direction);
        super.update();
    }

    @Override
    public void applyToShader(ShaderProgram program) {
        final int texNum = 3;
        fbo().getColorBufferTexture().bind(texNum);
        program.setUniformi("u_depthMapDir", texNum);
        program.setUniformMatrix("u_lightTrans", camera().combined);
        program.setUniformf("u_type", 1);
    }


}
