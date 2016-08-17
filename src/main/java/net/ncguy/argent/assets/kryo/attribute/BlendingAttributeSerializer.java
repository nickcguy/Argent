package net.ncguy.argent.assets.kryo.attribute;

import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Created by Guy on 01/08/2016.
 */
public class BlendingAttributeSerializer extends Serializer<BlendingAttribute> {

    @Override
    public void write(Kryo kryo, Output output, BlendingAttribute object) {
        output.writeString(ColorAttribute.getAttributeAlias(object.type));
        output.writeBoolean(object.blended);
        output.writeInt(object.sourceFunction);
        output.writeInt(object.destFunction);
        output.writeFloat(object.opacity);
    }

    @Override
    public BlendingAttribute read(Kryo kryo, Input input, Class<BlendingAttribute> type) {
        BlendingAttribute attr = new BlendingAttribute();
        attr.blended = input.readBoolean();
        attr.sourceFunction = input.readInt();
        attr.destFunction = input.readInt();
        attr.opacity = input.readFloat();
        return attr;
    }
}
