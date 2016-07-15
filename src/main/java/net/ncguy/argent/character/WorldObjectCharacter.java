package net.ncguy.argent.character;

import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.CapsuleShapeBuilder;
import com.badlogic.gdx.math.Vector3;
import net.ncguy.argent.physics.BulletEntity;
import net.ncguy.argent.utils.Reference;
import net.ncguy.argent.world.GameWorld;
import net.ncguy.argent.world.WorldObject;

/**
 * Created by Guy on 13/07/2016.
 */
public class WorldObjectCharacter extends Character {

    protected WorldObject body;

    public WorldObjectCharacter(WorldObject body) {
        this.body = body;
    }

    @Override
    public void move(Vector3 dir, float speed) {
        body.translate(dir.nor().scl(speed));
        body.transform();
    }

    @Override
    public void rotate(Vector3 flatForwardVector) {
        body.rotateTo(flatForwardVector);
    }

    @Override
    public Vector3 getTranslation() {
        return body.getTranslation();
    }

    public static WorldObjectCharacter createDefaultPlayerCharacter(GameWorld.Generic world) {
        modelBuilder.begin();
        MeshPartBuilder builder = modelBuilder.part("player_body", GL30.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates, Reference.Defaults.Models.material());
        CapsuleShapeBuilder.build(builder, 75, 500, 64);
        WorldObject obj = new WorldObject(world, new ModelInstance(modelBuilder.end()));
        if(world instanceof GameWorld.Physics) {
            world.addInstance(new BulletEntity<>(world, obj.transform(), obj));
        }else{
            world.addInstance(obj);
        }
        return new WorldObjectCharacter(obj);
    }

    private static ModelBuilder modelBuilder = new ModelBuilder();

}
