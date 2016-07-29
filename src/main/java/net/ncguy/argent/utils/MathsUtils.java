package net.ncguy.argent.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Guy on 27/07/2016.
 */
public class MathsUtils {

    public static float barryCentric(Vector3 p1, Vector3 p2, Vector3 p3, Vector2 pos) {
        float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
        float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
        float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
        float l3 = 1.0f - l1 - l2;
        return l1 * p1.y + l2 * p2.y + l3 * p3.y;
    }

    public static float dst(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
    }

    /**
     * Angle between 2 points.
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public static float angle(float x1, float y1, float x2, float y2) {
        return (float) Math.toDegrees(Math.atan2(x2 - x1, y2 - y1));
    }

}
