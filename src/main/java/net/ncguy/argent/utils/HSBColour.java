package net.ncguy.argent.utils;

import java.awt.*;

/**
 * Created by Guy on 27/06/2016.
 */
public class HSBColour {

    public float h;
    public float s, b;

    public HSBColour() {
        this(0f, 0f, 0f);
    }

    public HSBColour(float h, float s, float b) {
        this.h = h % 360;
        this.s = s % 1;
        this.b = b % 1;
    }

    public HSBColour(int h, int s, int b) {
        this((float)h, (float)s, (float)b);
    }

    public Color toRGB() {
        return Color.getHSBColor(h, s, b);
    }

    public static HSBColour fromRGB(Color colour) {
        return fromRGB(colour.getRed(), colour.getGreen(), colour.getBlue());
    }
    public static HSBColour fromRGB(int r, int g, int b) {
        float[] hsb = new float[3];
        Color.RGBtoHSB(r, g, b, hsb);
        return new HSBColour(hsb[0], hsb[1], hsb[2]);
    }

    @Override
    public String toString() {
        return String.format("[%s, %s, %s]", h, s, b);
    }

    public void set(HSBColour hsb) {
        this.h = hsb.h;
        this.s = hsb.s;
        this.b = hsb.b;
    }
}
