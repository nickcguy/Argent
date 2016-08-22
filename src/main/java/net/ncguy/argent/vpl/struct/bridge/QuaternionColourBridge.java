package net.ncguy.argent.vpl.struct.bridge;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Quaternion;

/**
 * Created by Guy on 22/08/2016.
 */
public class QuaternionColourBridge implements IStructBridge<Quaternion, Color> {

    @Override
    public boolean biDirectional() {
        return true;
    }

    @Override
    public Quaternion reverse(Color color) {
        return new Quaternion(color.r, color.g, color.b, color.a);
    }

    @Override
    public Color bridge(Quaternion quaternion) {
        return new Color(quaternion.x, quaternion.y, quaternion.z, quaternion.w);
    }
}
