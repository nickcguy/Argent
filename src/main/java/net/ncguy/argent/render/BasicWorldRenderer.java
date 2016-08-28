package net.ncguy.argent.render;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.world.GameWorld;

/**
 * Created by Guy on 15/07/2016.
 */
public class BasicWorldRenderer<T extends WorldEntity> extends AbstractWorldRenderer<T> {

    Environment environment;

    public BasicWorldRenderer(GameWorld<T> world) {
        super(world);
    }

    public Environment environment() {
        if(environment == null) {
            environment = new Environment();
            environment.set(new ColorAttribute(ColorAttribute.AmbientLight, .4f, .4f, .4f, 1));
            environment.add(new DirectionalLight().set(.8f, .8f, .8f, -1f, -.8f, -.2f));
        }
        return environment;
    }

    @Override
    public void render(ModelBatch batch, float delta) {
        batch.begin(camera());
        if(environment() != null)
            batch.render(world.instances(), environment());
        else
            batch.render(world.instances());
        additionalRenderers.forEach(r -> r.render(batch, delta));
        batch.end();
    }

    @Override public void dispose() {
        world.dispose();
    }
}
