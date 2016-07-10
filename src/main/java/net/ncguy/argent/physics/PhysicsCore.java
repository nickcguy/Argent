package net.ncguy.argent.physics;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import net.ncguy.argent.render.WorldRenderer;

/**
 * Created by Guy on 20/06/2016.
 */
public class PhysicsCore {

    public PhysicsCore() {
        Bullet.init();
    }

    public <T> void buildComplexCollisionMesh(WorldRenderer<T> renderer, T obj) {
        ModelInstance inst = renderer.getRenderable(obj);
        if(inst == null) return;

        btCollisionShape shape = Bullet.obtainStaticNodeShape(inst.model.nodes);
        if(shape != null)
            renderer.buildBulletCollision(obj, shape);
    }

    public <T> void buildComplexCollisionMeshes(WorldRenderer<T> renderer) {
        renderer.objects().forEach(o -> buildComplexCollisionMesh(renderer, o));
    }

}
