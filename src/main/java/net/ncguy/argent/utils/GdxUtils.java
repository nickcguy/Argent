package net.ncguy.argent.utils;

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

}
