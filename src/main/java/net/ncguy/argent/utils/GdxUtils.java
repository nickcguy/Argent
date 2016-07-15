package net.ncguy.argent.utils;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * Created by Guy on 11/07/2016.
 */
public class GdxUtils {

    public static void keepWithinParent(Actor actor) {
        if(actor.hasParent()) keepWithinActor(actor, actor.getParent());
    }

    public static void keepWithinStage(Actor actor) {
        Stage stage = actor.getStage();
        if(stage != null) {
            Actor dummy = new Actor();
            dummy.setPosition(0, 0);
            dummy.setSize(stage.getWidth(), stage.getHeight());
            keepWithinActor(actor, dummy);
        }
    }

    public static void keepWithinActor(Actor actor, Actor grp) {
        float x = actor.getX(), y = actor.getY();
        float w = actor.getWidth(), h = actor.getHeight();

        if(x < grp.getX()) x = grp.getX();
        if(y < grp.getY()) y = grp.getY();
        if(x+w > grp.getX()+grp.getWidth()) x = grp.getWidth()-w;
        if(y+h > grp.getY()+grp.getHeight()) y = grp.getHeight()-h;

        actor.setBounds(Math.round(x), Math.round(y), Math.round(w), Math.round(h));
    }

    public static Vector3 getRightVector(Camera camera) {
        return getRightVector(camera.direction.cpy(), camera.up.cpy());
    }

    public static Vector3 getRightVector(Vector3 forward, Vector3 up) {
        return forward.crs(up);
    }

    public static Vector3 getFlatForwardVector(Camera camera) {
        return getFlatForwardVector(camera.direction);
    }

    public static Vector3 getFlatForwardVector(Vector3 forward) {
        Vector3 flat = forward.cpy();
        flat.y = 0;
        return flat;
    }

    public static Vector3 getFlatRightVector(Camera camera) {
        return getRightVector(getFlatForwardVector(camera), camera.up);
    }

}
