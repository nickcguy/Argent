package net.ncguy.argent.world;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

import static com.badlogic.gdx.physics.bullet.collision.btCollisionObject.CollisionFlags.*;

/**
 * Created by Guy on 20/06/2016.
 */
public class WorldObject extends Entity {

    // BULLET

    public btRigidBody body;
    public btCollisionShape shape;
    public float mass = 1;

    public void setStatic() {
        if(body == null) return;
        body.setCollisionFlags(CF_STATIC_OBJECT);
        body.setActivationState(Collision.ISLAND_SLEEPING);
    }

    public void setKinematic() {
        if(body == null) return;
        body.setCollisionFlags(CF_KINEMATIC_OBJECT);
        body.setActivationState(Collision.ACTIVE_TAG);
    }

    public void setDynamic() {
        if(body == null) return;
        body.setCollisionFlags(CF_CUSTOM_MATERIAL_CALLBACK);
        body.setActivationState(Collision.ACTIVE_TAG);
    }

    public void setWorldFlag(GameWorld.Physics.WorldFlags flag) {
        body.setContactCallbackFlag(flag.flag);
    }

    public void setWorldFilters(int filters) {
        if(body == null) return;
        body.setContactCallbackFilter(filters);
    }
    public void setWorldFilters(GameWorld.Physics.WorldFlags... filters) {
        if(body == null) return;
        int flag = 0;
        for (GameWorld.Physics.WorldFlags filter : filters)
            flag = flag | filter.flag;
        setWorldFilters(flag);
    }

}
