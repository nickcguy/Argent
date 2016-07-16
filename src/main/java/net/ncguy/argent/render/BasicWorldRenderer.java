package net.ncguy.argent.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import net.ncguy.argent.world.GameWorld;

/**
 * Created by Guy on 15/07/2016.
 */
public class BasicWorldRenderer<T extends RenderableProvider> extends AbstractWorldRenderer<T> {

    Environment environment;

    public BasicWorldRenderer(GameWorld<T> world) {
        super(world);
    }

    public Environment environment() {
        if(environment == null) {
            environment = new Environment();
            environment.add(new DirectionalLight().set(Color.WHITE, Vector3.X));
        }
        return environment;
    }

    @Override
    public void render(float delta) {
        batch().begin(camera());
        batch().render(world.instances(), environment());
        additionalRenderers.forEach(r -> r.render(batch(), delta));
        batch().end();
    }

}
