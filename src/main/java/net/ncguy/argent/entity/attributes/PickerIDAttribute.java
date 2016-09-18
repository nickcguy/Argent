package net.ncguy.argent.entity.attributes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Attribute;
import net.ncguy.argent.entity.WorldEntity;

/**
 * Created by Guy on 30/07/2016.
 */
public class PickerIDAttribute extends Attribute {

    public static final String Alias = "weID";
    public static final long Type = register(Alias);

    public Color colour = Color.WHITE.cpy();

    public static boolean is(final long mask) {
        return (mask & Type) == mask;
    }

    public PickerIDAttribute() {
        super(Type);
    }

    public PickerIDAttribute(PickerIDAttribute other) {
        super(Type);
        this.colour.set(other.colour);
    }

    @Override
    public Attribute copy() {
        return new PickerIDAttribute(this);
    }

    @Override
    public int hashCode() {
        return colour.hashCode();
    }

    @Override
    public int compareTo(Attribute o) {
        return -1;
    }

    @Override
    public String toString() {
        return String.format("WorldEntityIDAttribute{r=%s, g=%s, b=%s}", colour.r, colour.g, colour.b);
    }

    public static int decode(int rgba8888) {
        int id = (rgba8888 & 0xFF000000) >>> 24;
        id += ((rgba8888 & 0x00FF0000) >>> 16) * 256;
        id += ((rgba8888 & 0x0000FF00) >>> 8) * 256 * 256;
        id += ((rgba8888 & 0x000000FF)) * 256 * 256 * 256;

        return id;
    }

    public static PickerIDAttribute encodeWorldEntity(WorldEntity e) {
        PickerIDAttribute attr = new PickerIDAttribute();
        encodeID(e.id(), attr);
        return attr;
    }

    public static void encodeID(Color colour, PickerIDAttribute out) {
        encodeID(colour.toIntBits(), out);
    }

    public static void encodeID(int id, PickerIDAttribute out) {
        out.colour.r = id & 0x000000FF;
        out.colour.g = (id & 0x0000FF00) >>> 8;
        out.colour.b = (id & 0x00FF0000) >>> 16;

        out.colour.r /= 255;
        out.colour.g /= 255;
        out.colour.b /= 255;
    }

}
