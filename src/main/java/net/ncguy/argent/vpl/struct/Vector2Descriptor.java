package net.ncguy.argent.vpl.struct;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import net.ncguy.argent.vpl.IStructDescriptor;
import net.ncguy.argent.vpl.annotations.NodeData;
import net.ncguy.argent.vpl.VPLNode;

/**
 * Created by Guy on 19/08/2016.
 */
public class Vector2Descriptor extends IStructDescriptor {

    public Vector2Descriptor() { colour = Color.BLUE; }

    @NodeData(value = "Make Vector2", execIn = false, execOut = false, argNames = {"X", "Y"})
    public static Vector2 makeStruct(VPLNode node, int pinId, float x, float y) {
        return new Vector2(x, y);
    }

    @NodeData(value = "Break Vector2", execIn = false, execOut = false, outPins = 2, argNames = "Vector2")
    public static float breakStruct(VPLNode node, int pinId, Vector2 vec) {
        switch (MathUtils.clamp(pinId, 0, 1)) {
            case 0: return vec.x;
            case 1: return vec.y;
        }
        return vec.x;
    }

}
