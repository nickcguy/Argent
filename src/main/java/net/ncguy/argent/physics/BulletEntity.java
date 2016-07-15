package net.ncguy.argent.physics;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import net.ncguy.argent.core.Meta;
import net.ncguy.argent.editor.ConfigurableAttribute;
import net.ncguy.argent.editor.IConfigurable;
import net.ncguy.argent.editor.shared.config.ConfigControl;
import net.ncguy.argent.world.GameWorld;

import java.util.ArrayList;
import java.util.List;

import static com.badlogic.gdx.physics.bullet.collision.btCollisionObject.CollisionFlags.*;
import static net.ncguy.argent.physics.BulletEntity.PhysicsState.*;

/**
 * Created by Guy on 14/07/2016.
 */
public class BulletEntity<T> implements IConfigurable {

    protected GameWorld.Generic<BulletEntity<T>> gameWorld;
    protected Matrix4 transform;
    protected T instance;

    private float mass = 1;
    private PhysicsState physicsState = STATIC;

    public float mass() { return mass; }
    public T instance() { return instance; }

    public BulletEntity(GameWorld.Generic<BulletEntity<T>> gameWorld, Matrix4 transform, T instance) {
        this.gameWorld = gameWorld;
        this.transform = transform;
        this.instance = instance;
        setStatic();
    }

    public BulletEntity(Matrix4 transform, T instance) {
        this(null, transform, instance);
    }

    public GameWorld.Generic<BulletEntity<T>> gameWorld() { return gameWorld; }

    public BulletEntity gameWorld(GameWorld.Generic<BulletEntity<T>> gameWorld) {
        this.gameWorld = gameWorld;
        return this;
    }

    // BULLET

    public void reconstructBody() {
        if(body != null) {
            body.dispose();
            body = null;
        }
        btCollisionShape shape = Bullet.obtainStaticNodeShape(gameWorld.renderer().getRenderable(this).nodes);
        if(shape != null)
            gameWorld.renderer().buildBulletCollision(this, shape);
        if(body != null) {
            btMotionState state = body.getMotionState();
            if(state instanceof DefaultMotionState) {
                ((DefaultMotionState)state).transform = this.transform;
            }
        }
        switch(physicsState) {
            case STATIC: setStatic(); break;
            case KINEMATIC: setKinematic(); break;
            case DYNAMIC: setDynamic(); break;
        }
        body.setWorldTransform(transform);
        gameWorld.addInstance(this);
    }

    public transient btRigidBody body;
    public transient btCollisionShape shape;

    public void setStatic() {
        if(body == null) return;
        body.setCollisionFlags(CF_STATIC_OBJECT);
        body.setActivationState(Collision.DISABLE_DEACTIVATION);
        physicsState = STATIC;
    }

    public void setKinematic() {
        if(body == null) return;
        body.setCollisionFlags(CF_KINEMATIC_OBJECT);
        body.setActivationState(Collision.DISABLE_DEACTIVATION);
        physicsState = KINEMATIC;
    }

    public void setDynamic() {
        if(body == null) return;
        body.setCollisionFlags(CF_CUSTOM_MATERIAL_CALLBACK);
        body.activate(true);
        body.setActivationState(Collision.DISABLE_DEACTIVATION);
        physicsState = DYNAMIC;
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

    @Override
    public List<ConfigurableAttribute<?>> getConfigurableAttributes() {
        List<ConfigurableAttribute<?>> attrs = new ArrayList<>();
        ConfigurableAttribute<PhysicsState> physicsStateAttr = attr(attrs, new Meta.Object("Physics State", "Physics"), () -> physicsState, (var) -> {
            switch(var) {
                case STATIC: setStatic(); return;
                case KINEMATIC: setKinematic(); return;
                case DYNAMIC: setDynamic(); return;
            }
        }, ConfigControl.COMBOBOX, PhysicsState::valueOf);
        physicsStateAttr.addParam("items", PhysicsState[].class, PhysicsState.values());
        attr(attrs, new Meta.Object("Mass", "Physics"), () -> mass, (val) -> mass = val, ConfigControl.NUMBERSELECTOR, Float::parseFloat);

        if(this.instance instanceof IConfigurable)
            attrs.addAll(((IConfigurable)this.instance).getConfigAttrs());

        return attrs;
    }

    public PhysicsState physicsState() {
        return physicsState;
    }


    public enum PhysicsState {
        STATIC,
        KINEMATIC,
        DYNAMIC
    }

}
