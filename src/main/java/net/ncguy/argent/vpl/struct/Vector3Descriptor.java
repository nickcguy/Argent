package net.ncguy.argent.vpl.struct;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import net.ncguy.argent.vpl.IStructDescriptor;
import net.ncguy.argent.vpl.annotations.NodeData;
import net.ncguy.argent.vpl.VPLNode;

/**
 * Created by Guy on 19/08/2016.
 */
public class Vector3Descriptor extends IStructDescriptor {

    public Vector3Descriptor() {
        colour = Color.BLUE;
    }

    @NodeData(value = "Make Vector3", execIn = false, execOut = false, argNames = {"X", "Y", "Z"})
    public static Vector3 makeStruct(VPLNode node, int pinId, float x, float y, float z) {
        return new Vector3(x, y, z);
    }

    @NodeData(value = "Break Vector3", execIn = false, execOut = false, outPins = 3, argNames = "Vector 3")
    public static float breakStruct(VPLNode node, int pinId, Vector3 vec) {
        switch (MathUtils.clamp(pinId, 0, 2)) {
            case 0: return vec.x;
            case 1: return vec.y;
            case 2: return vec.z;
        }
        return vec.x;
    }

}
