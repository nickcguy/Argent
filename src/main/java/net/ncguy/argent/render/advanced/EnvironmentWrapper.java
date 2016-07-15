package net.ncguy.argent.render.advanced;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

/**
 * Created by Guy on 15/07/2016.
 */
public class EnvironmentWrapper {

    String id;
    Environment environment;
    BoundingBox bounds;

    public boolean isInBounds(Vector3 point) {
        return bounds.contains(point);
    }

}
