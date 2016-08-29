package net.ncguy.argent.vpl.struct;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import net.ncguy.argent.vpl.IStructDescriptor;
import net.ncguy.argent.vpl.VPLNode;
import net.ncguy.argent.vpl.annotations.NodeData;

/**
 * Created by Guy on 19/08/2016.
 */
public class ColourDescriptor extends IStructDescriptor {

    public ColourDescriptor() {
        colour = Color.BLUE;
    }

    @NodeData(value = "Make Colour", execIn = false, execOut = false, argNames = {"Red", "Green", "Blue", "Alpha"})
    public static Color makeStruct(VPLNode node, int pinId, float r, float g, float b, float a) {
        return new Color(r, g, b, a);
    }

    @NodeData(value = "Break Colour", execIn = false, execOut = false, outPins = 4, argNames = "Colour")
    public static Float breakStruct(VPLNode node, int pinId, Color col) {
        switch (MathUtils.clamp(pinId, 0, 3)) {
            case 0: return col.r;
            case 1: return col.g;
            case 2: return col.b;
            case 3: return col.a;
        }
        return col.r;
    }
}
