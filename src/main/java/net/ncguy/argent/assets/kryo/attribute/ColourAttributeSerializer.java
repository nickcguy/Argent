package net.ncguy.argent.assets.kryo.attribute;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Created by Guy on 01/08/2016.
 */
public class ColourAttributeSerializer extends Serializer<ColorAttribute> {

    @Override
    public void write(Kryo kryo, Output output, ColorAttribute object) {
        output.writeString(ColorAttribute.getAttributeAlias(object.type));
        output.writeInt(Color.rgba8888(object.color));
    }

    @Override
    public ColorAttribute read(Kryo kryo, Input input, Class<ColorAttribute> type) {
        String alias = input.readString();
        int rgba8888 = input.readInt();
        Color colour = new Color();
        Color.rgba8888ToColor(colour, rgba8888);
        return new ColorAttribute(ColorAttribute.getAttributeType(alias), colour);
    }
}
