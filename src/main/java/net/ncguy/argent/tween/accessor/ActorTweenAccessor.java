package net.ncguy.argent.tween.accessor;

import aurelienribon.tweenengine.TweenAccessor;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by Guy on 27/08/2016.
 */
public class ActorTweenAccessor implements TweenAccessor<Actor> {

    public static final int X = 1;
    public static final int Y = 2;
    public static final int W = 4;
    public static final int H = 8;

    public boolean is(int type, int mask) {
        return (mask & type) != 0;
    }


    @Override
    public int getValues(Actor actor, int i, float[] floats) {
        int index = 0;
        if(is(X, i)) floats[index++] = actor.getX();
        if(is(Y, i)) floats[index++] = actor.getY();
        if(is(W, i)) floats[index++] = actor.getWidth();
        if(is(H, i)) floats[index++] = actor.getHeight();
        return index;
    }

    @Override
    public void setValues(Actor actor, int i, float[] floats) {
        int index = 0;
        if(is(X, i)) actor.setX(floats[index++]);
        if(is(Y, i)) actor.setY(floats[index++]);
        if(is(W, i)) actor.setWidth(floats[index++]);
        if(is(H, i)) actor.setHeight(floats[index++]);
    }
}
