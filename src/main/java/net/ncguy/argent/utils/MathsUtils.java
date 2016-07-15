package net.ncguy.argent.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Guy on 07/07/2016.
 */
public class MathsUtils {

    public static int tMin(int a, int b, int c) {
        return Math.min(a, Math.min(b, c));
    }
    public static long tMin(long a, long b, long c) {
        return Math.min(a, Math.min(b, c));
    }
    public static float tMin(float a, float b, float c) {
        return Math.min(a, Math.min(b, c));
    }
    public static double tMin(double a, double b, double c) {
        return Math.min(a, Math.min(b, c));
    }

    public static void oneMinus(Vector3 vec) {
        vec.x = 1-vec.x;
        vec.y = 1-vec.y;
        vec.z = 1-vec.z;
    }

    public static Color randomColour() {
        return new Color((float)Math.random(), (float)Math.random(), (float)Math.random(), 1);
    }

    public static float barryCentric(Vector3 p1, Vector3 p2, Vector3 p3, Vector2 pos) {
        float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
        float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
        float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
        float l3 = 1.0f - l1 - l2;
        return l1 * p1.y + l2 * p2.y + l3 * p3.y;
    }
}
