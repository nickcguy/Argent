package net.ncguy.argent.render.advanced;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Vector3;
import net.ncguy.argent.render.BasicWorldRenderer;
import net.ncguy.argent.world.GameWorld;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guy on 15/07/2016.
 */
public class AdvancedWorldRenderer<T extends RenderableProvider> extends BasicWorldRenderer<T> {

    List<EnvironmentWrapper> environments;

    public AdvancedWorldRenderer(GameWorld<T> world) {
        super(world);
        environments = new ArrayList<>();
    }

    @Override
    public void render(float delta) {
        batch().begin(camera());
        batch().render(world.instances(), getEnv(camera().position));
        batch().end();
    }

    public Environment getEnv(Vector3 point) {
        for (EnvironmentWrapper wrapper : environments) {
            if(wrapper.isInBounds(point)) return wrapper.environment;
        }
        return environment();
    }

}
