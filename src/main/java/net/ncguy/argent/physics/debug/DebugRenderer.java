package net.ncguy.argent.physics.debug;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.collision.btCollisionWorld;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;

/**
 * Created by Guy on 21/06/2016.
 */
public class DebugRenderer {

    private DebugDrawer drawer;
    private btCollisionWorld world;

    public DebugRenderer(btCollisionWorld world) {
        this.world = world;
        this.drawer = new DebugDrawer();
        this.drawer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_DrawNormals);
        this.world.setDebugDrawer(this.drawer);
    }

    public void update(Camera camera) {
        this.drawer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_DrawAabb);
        this.drawer.begin(camera);
        world.debugDrawWorld();
        this.drawer.end();
    }

}
