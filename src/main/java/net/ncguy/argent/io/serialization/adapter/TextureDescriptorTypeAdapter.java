package net.ncguy.argent.io.serialization.adapter;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.ncguy.argent.Argent;
import net.ncguy.argent.utils.SpriteCache;

import java.io.IOException;

/**
 * Created by Guy on 11/07/2016.
 */
public class TextureDescriptorTypeAdapter extends TypeAdapter<TextureDescriptor<Texture>> {

    /*
       T texture = null;
	   Texture.TextureFilter minFilter;
	   Texture.TextureFilter magFilter;
	   Texture.TextureWrap uWrap;
	   Texture.TextureWrap vWrap;
     */

    @Override
    public void write(JsonWriter out, TextureDescriptor<Texture> value) throws IOException {
        if(value == null) { out.nullValue(); return; }
        assertTextureDescriptor(value);
        out.beginObject();
        out.name("textureRef").value(Argent.content.getRef(value.texture));
//        out.name("textureRef").value(value.texture.toString());
        out.name("minFilter").value(value.minFilter.name());
        out.name("magFilter").value(value.magFilter.name());
        out.name("uWrap").value(value.uWrap.name());
        out.name("vWrap").value(value.vWrap.name());

        out.endObject();
    }

    private void assertTextureDescriptor(TextureDescriptor<Texture> value) {
        if(value.texture == null) value.texture = SpriteCache.pixel();
        if(value.minFilter == null) value.minFilter = Texture.TextureFilter.Linear;
        if(value.magFilter == null) value.magFilter = Texture.TextureFilter.Linear;
        if(value.uWrap == null) value.uWrap = Texture.TextureWrap.ClampToEdge;
        if(value.vWrap == null) value.vWrap = Texture.TextureWrap.ClampToEdge;
    }

    @Override
    public TextureDescriptor<Texture> read(JsonReader in) throws IOException {
        TextureDescriptor<Texture> descriptor = new TextureDescriptor<>();
        String[] refs = new String[5];
        in.beginObject();
        while(in.hasNext()) {
            switch(in.nextName()) {
                case "textureRef": refs[0] = in.nextString(); break;
                case "minFilter": refs[1] = in.nextString(); break;
                case "magFilter": refs[2] = in.nextString(); break;
                case "uWrap": refs[3] = in.nextString(); break;
                case "vWrap": refs[4] = in.nextString(); break;
            }
        }
        in.endObject();
        descriptor.texture      = Argent.content.get(refs[0], Texture.class);
        descriptor.minFilter    = Texture.TextureFilter.valueOf(refs[1]);
        descriptor.magFilter    = Texture.TextureFilter.valueOf(refs[2]);
        descriptor.uWrap        = Texture.TextureWrap.valueOf(refs[3]);
        descriptor.vWrap        = Texture.TextureWrap.valueOf(refs[4]);
        return descriptor;
    }
}
