package net.ncguy.argent.vpl.struct;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import net.ncguy.argent.vpl.IStructDescriptor;
import net.ncguy.argent.vpl.NodeData;

/**
 * Created by Guy on 19/08/2016.
 */
public class Vector3Descriptor implements IStructDescriptor<Vector3, Float> {

    @Override
    public Color colour() {
        return Color.BLUE;
    }

    @Override
    @NodeData(value = "Make Vector3", execIn = false, execOut = false)
    public Vector3 makeStruct(Float[] args) {
        Vector3 vec = new Vector3();
        if(args.length >= 1) vec.x = args[0];
        if(args.length >= 2) vec.y = args[1];
        if(args.length >= 3) vec.z = args[2];
        return vec;
    }

    @Override
    @NodeData(value = "Break Vector3", execIn = false, execOut = false)
    public Float breakStruct(Vector3 vec, int pinId) {
        switch (MathUtils.clamp(pinId, 0, outPins()-1)) {
            case 0: return vec.x;
            case 1: return vec.y;
            case 2: return vec.z;
        }
        return vec.x;
    }

    @Override
    public int outPins() {
        return 3;
    }
}
