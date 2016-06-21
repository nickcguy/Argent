package net.ncguy.argent.world;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import net.ncguy.argent.Argent;
import net.ncguy.argent.physics.DefaultMotionState;
import net.ncguy.argent.render.WorldRenderer;
import net.ncguy.argent.world.components.RenderingComponent;
import net.ncguy.argent.world.components.TransformComponent;

import java.util.List;
import java.util.Optional;

import static net.ncguy.argent.world.GameWorldFactory.ComponentMappers.renderingComponent;
import static net.ncguy.argent.world.GameWorldFactory.ComponentMappers.transformComponent;

/**
 * Created by Guy on 21/06/2016.
 */
public class GameWorldFactory {

    public static GameWorld.Generic<WorldObject> worldObjectWorld(List<WorldObject> instances) {
        WorldRenderer<WorldObject> renderer = new WorldRenderer<WorldObject>(instances) {
            @Override
            public ModelInstance getRenderable(WorldObject obj) {
                RenderingComponent ren = renderingComponent.get(obj);
                if (ren != null)
                    return ren.instance();
                return null;
            }

            @Override
            public void buildBulletCollision(WorldObject obj, btCollisionShape shape) {
            }
        };
        return new GameWorld.Generic<>(renderer, instances);
    }

    public static GameWorld.Physics<WorldObject> worldObjectPhysics(List<WorldObject> instances) {
        WorldRenderer<WorldObject> renderer = new WorldRenderer<WorldObject>(instances) {
            @Override
            public ModelInstance getRenderable(WorldObject obj) {
                RenderingComponent ren = renderingComponent.get(obj);
                if (ren != null)
                    return ren.instance();
                return null;
            }

            @Override
            public void buildBulletCollision(WorldObject obj, btCollisionShape shape) {
                TransformComponent trans = transformComponent.get(obj);
                if (trans == null) return;
                DefaultMotionState motionState = new DefaultMotionState();
                motionState.transform = trans.transform;
                Vector3 inertia = new Vector3();
                if (obj.mass > 0)
                    shape.calculateLocalInertia(obj.mass, inertia);
                btRigidBody.btRigidBodyConstructionInfo info = new btRigidBody.btRigidBodyConstructionInfo(obj.mass, motionState, shape, inertia);
                obj.body = new btRigidBody(info);
                obj.body.userData = obj;
                obj.shape = shape;
                info.dispose();
            }

            @Override
            public btRigidBody getBulletBody(WorldObject obj) {
                if(obj.body == null) {
                    Optional<ModelInstance> inst = getRenderableOptional(obj);
                    if (inst.isPresent())
                        Argent.physics.buildComplexCollisionMesh(this, obj);
                }
                return obj.body;
            }
        };
        return new GameWorld.Physics<>(renderer, instances);
    }

    public static class ComponentMappers {

        public static final ComponentMapper<RenderingComponent> renderingComponent = ComponentMapper.getFor(RenderingComponent.class);
        public static final ComponentMapper<TransformComponent> transformComponent = ComponentMapper.getFor(TransformComponent.class);

    }

}
