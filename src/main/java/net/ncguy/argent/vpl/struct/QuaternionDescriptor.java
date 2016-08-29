package net.ncguy.argent.vpl.struct;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Quaternion;
import net.ncguy.argent.vpl.IStructDescriptor;
import net.ncguy.argent.vpl.annotations.NodeData;
import net.ncguy.argent.vpl.VPLNode;

/**
 * Created by Guy on 22/08/2016.
 */
public class QuaternionDescriptor extends IStructDescriptor {

    @Override
    public Color colour() {
        return Color.VIOLET;
    }

    @NodeData(value = "Make Quaternion", execIn = false, execOut = false, argNames = {"X", "Y", "Z", "W"})
    public static Quaternion makeStruct(VPLNode node, int pinId, float x, float y, float z, float w) {
        return new Quaternion(x, y, z, w);
    }

    @NodeData(value = "Break Quaternion", execIn = false, execOut = false, outPins = 4, argNames = "Quaternion")
    public static Float breakStruct(VPLNode node, int pinId, Quaternion vec) {
        switch (MathUtils.clamp(pinId, 0, 3)) {
            case 0: return vec.x;
            case 1: return vec.y;
            case 2: return vec.z;
            case 3: return vec.w;
        }
        return vec.x;
    }

}
