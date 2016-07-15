package net.ncguy.argent.physics;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
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

    public btCollisionObject buildComplexCollisionMesh(ModelInstance inst) {
        btCollisionShape shape = Bullet.obtainStaticNodeShape(inst.model.nodes);
        if(shape != null) {
            btRigidBody.btRigidBodyConstructionInfo info = new btRigidBody.btRigidBodyConstructionInfo(0, null, shape, Vector3.Zero);
            btRigidBody body = new btRigidBody(info);
            info.dispose();
            return body;
        }
        return null;
    }

}
