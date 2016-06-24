package net.ncguy.argent.io.serialization.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.ncguy.argent.render.shader.DynamicShader;

import java.io.IOException;

/**
 * Created by Guy on 23/06/2016.
 */
public class DynamicShaderInfoTypeAdapter extends TypeAdapter<DynamicShader.Info> {

    @Override
    public void write(JsonWriter out, DynamicShader.Info value) throws IOException {
        out.beginObject();
        out.name("name").value(value.name);
        out.name("vertex").value(value.vertex);
        out.name("fragment").value(value.fragment);
        out.endObject();
    }

    @Override
    public DynamicShader.Info read(JsonReader in) throws IOException {
        DynamicShader.Info info = new DynamicShader.Info();
        in.beginObject();
        while(in.hasNext()) {
            switch(in.nextName()) {
                case "name":        info.name = in.nextString();     break;
                case "vertex":      info.vertex = in.nextString();   break;
                case "fragment":    info.fragment = in.nextString(); break;
            }
        }
        in.endObject();
        return null;
    }
}
