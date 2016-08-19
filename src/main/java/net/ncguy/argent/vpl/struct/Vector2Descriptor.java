package net.ncguy.argent.vpl.struct;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import net.ncguy.argent.vpl.IStructDescriptor;
import net.ncguy.argent.vpl.NodeData;

/**
 * Created by Guy on 19/08/2016.
 */
public class Vector2Descriptor implements IStructDescriptor<Vector2, Float> {

    @Override
    public Color colour() {
        return Color.BLUE;
    }

    @Override
    @NodeData(value = "Make Vector2", execIn = false, execOut = false)
    public Vector2 makeStruct(Float[] args) {
        Vector2 vec = new Vector2();
        if(args.length >= 1) vec.x = args[0];
        if(args.length >= 2) vec.y = args[1];
        return vec;
    }

    @Override
    @NodeData(value = "Break Vector2", execIn = false, execOut = false)
    public Float breakStruct(Vector2 vec, int pinId) {
        switch (MathUtils.clamp(pinId, 0, outPins()-1)) {
            case 0: return vec.x;
            case 1: return vec.y;
        }
        return vec.x;
    }

    @Override
    public int outPins() {
        return 2;
    }
}
