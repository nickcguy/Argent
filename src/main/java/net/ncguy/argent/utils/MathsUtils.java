package net.ncguy.argent.utils;

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

}
