package net.ncguy.argent.tween.accessor;

import aurelienribon.tweenengine.TweenAccessor;
import com.badlogic.gdx.graphics.Color;

/**
 * Created by Guy on 22/08/2016.
 */
public class ColorTweenAccessor implements TweenAccessor<Color> {

    public static final int RED     = 1;
    public static final int GREEN   = 2;
    public static final int BLUE    = 4;
    public static final int ALPHA   = 8;

    public static final int MASK = RED | GREEN | BLUE | ALPHA;

    public static final int CHANNELS = 4;

    @Override
    public int getValues(Color color, int i, float[] floats) {
        int index = 0;
        if(is(RED, i)) floats[index++] = color.r;
        if(is(GREEN, i)) floats[index++] = color.g;
        if(is(BLUE, i)) floats[index++] = color.b;
        if(is(ALPHA, i)) floats[index++] = color.a;
        return index;
    }

    @Override
    public void setValues(Color color, int i, float[] floats) {
        int index = 0;
        if(is(RED, i))   color.r = floats[index++];
        if(is(GREEN, i)) color.g = floats[index++];
        if(is(BLUE, i))  color.b = floats[index++];
        if(is(ALPHA, i)) color.a = floats[index++];
    }

    public boolean is(int type, int mask) {
        return (mask & type) != 0;
    }

}
