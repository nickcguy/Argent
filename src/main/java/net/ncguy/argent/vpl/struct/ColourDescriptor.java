package net.ncguy.argent.vpl.struct;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import net.ncguy.argent.vpl.IStructDescriptor;
import net.ncguy.argent.vpl.NodeData;

/**
 * Created by Guy on 19/08/2016.
 */
public class ColourDescriptor implements IStructDescriptor<Color, Float> {

    @Override
    public Color colour() {
        return Color.BLUE;
    }

    @Override
    @NodeData(value = "Make Colour", execIn = false, execOut = false)
    public Color makeStruct(Float[] args) {
        Color vec = new Color();
        if(args.length >= 1) vec.r = args[0];
        if(args.length >= 2) vec.g = args[1];
        if(args.length >= 3) vec.b = args[2];
        if(args.length >= 4) vec.a = args[3];
        return vec;
    }

    @Override
    @NodeData(value = "Break Colour", execIn = false, execOut = false)
    public Float breakStruct(Color vec, int pinId) {
        switch (MathUtils.clamp(pinId, 0, outPins()-1)) {
            case 0: return vec.r;
            case 1: return vec.g;
            case 2: return vec.b;
            case 3: return vec.a;
        }
        return vec.r;
    }

    @Override
    public int outPins() {
        return 4;
    }
}
