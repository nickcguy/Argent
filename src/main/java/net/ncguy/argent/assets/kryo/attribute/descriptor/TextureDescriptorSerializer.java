package net.ncguy.argent.assets.kryo.attribute.descriptor;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import net.ncguy.argent.Argent;
import net.ncguy.argent.utils.TextureCache;


/**
 * Created by Guy on 01/08/2016.
 */
public class TextureDescriptorSerializer extends Serializer<TextureDescriptor> {

    @Override
    public void write(Kryo kryo, Output output, TextureDescriptor object) {
        // TODO Strengthen texture reference
        String ref = Argent.content.getRef(object.texture);
        if(ref == null) {
            System.err.printf("Error serializing TextureDescriptor [%s], unable to find texture reference\n", object.toString());
            ref = "cache_white";
        }
        pad(object);
        output.writeString(object.minFilter.name());
        output.writeString(object.magFilter.name());
        output.writeString(object.uWrap.name());
        output.writeString(object.vWrap.name());
        output.writeString(ref);
    }

    @Override
    public TextureDescriptor<Texture> read(Kryo kryo, Input input, Class<TextureDescriptor> type) {
        TextureDescriptor<Texture> descriptor = new TextureDescriptor<>();
        descriptor.minFilter = Texture.TextureFilter.valueOf(input.readString());
        descriptor.magFilter = Texture.TextureFilter.valueOf(input.readString());
        descriptor.uWrap = Texture.TextureWrap.valueOf(input.readString());
        descriptor.vWrap = Texture.TextureWrap.valueOf(input.readString());
        String ref = input.readString();
        Texture tex;
        if(ref.toLowerCase().startsWith("cache_")) {
            tex = TextureCache.get(ref);
        }else{
            tex = Argent.content.get(ref, Texture.class);
        }
        descriptor.texture = tex;
        return descriptor;
    }

    private void pad(TextureDescriptor descriptor) {
        if(descriptor.minFilter == null) descriptor.minFilter = Texture.TextureFilter.Linear;
        if(descriptor.magFilter == null) descriptor.magFilter = Texture.TextureFilter.Linear;
        if(descriptor.uWrap == null) descriptor.uWrap = Texture.TextureWrap.Repeat;
        if(descriptor.vWrap == null) descriptor.vWrap = Texture.TextureWrap.Repeat;
    }

}
