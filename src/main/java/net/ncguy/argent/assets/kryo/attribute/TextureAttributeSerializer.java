package net.ncguy.argent.assets.kryo.attribute;

import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import net.ncguy.argent.assets.kryo.attribute.descriptor.TextureDescriptorSerializer;

/**
 * Created by Guy on 01/08/2016.
 */
public class TextureAttributeSerializer extends Serializer<TextureAttribute> {

    @Override
    public void write(Kryo kryo, Output output, TextureAttribute object) {
        output.writeString(ColorAttribute.getAttributeAlias(object.type));
        output.writeFloat(object.offsetU);
        output.writeFloat(object.offsetV);
        output.writeFloat(object.scaleU);
        output.writeFloat(object.scaleV);
        output.writeInt(object.uvIndex);
        TextureDescriptorSerializer s = (TextureDescriptorSerializer) kryo.getSerializer(TextureDescriptor.class);
        s.write(kryo, output, object.textureDescription);
    }

    @Override
    public TextureAttribute read(Kryo kryo, Input input, Class<TextureAttribute> type) {
        String alias = input.readString();
        TextureAttribute attr = new TextureAttribute(TextureAttribute.getAttributeType(alias));
        attr.offsetU = input.readFloat();
        attr.offsetV = input.readFloat();
        attr.scaleU = input.readFloat();
        attr.scaleV = input.readFloat();
        attr.uvIndex = input.readInt();
        TextureDescriptorSerializer s = (TextureDescriptorSerializer) kryo.getSerializer(TextureDescriptor.class);
        attr.textureDescription.set(s.read(kryo, input, TextureDescriptor.class));
        return attr;
    }
}
