package net.ncguy.argent.world;

import com.badlogic.gdx.utils.Disposable;
import org.ode4j.ode.DSpace;
import org.ode4j.ode.DWorld;
import org.ode4j.ode.OdeHelper;

/**
 * Created by Guy on 27/10/2016.
 */
public class PhysicsWorldWrapper implements Disposable {

    private GameWorld world;
    private DWorld physicsWorld;
    private DSpace physicsSpace;

    public PhysicsWorldWrapper(GameWorld world) {
        this.world = world;
        this.physicsWorld = OdeHelper.createWorld();
        this.physicsSpace = OdeHelper.createSimpleSpace();
    }

    @Override
    public void dispose() {
        world.dispose();
    }
}
