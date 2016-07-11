package net.ncguy.argent.io.serialization.adapter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.ncguy.argent.Argent;
import net.ncguy.argent.io.serialization.JSONSerializer;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Guy on 11/07/2016.
 */
public class MaterialTypeAdapter extends TypeAdapter<Material> {

    @Override
    public void write(final JsonWriter out, final Material value) throws IOException {
        if(value == null) { out.nullValue(); return; }
        out.beginObject();
            out.name("id").value(value.id);
            ArrayList<Attribute> attrList = new ArrayList<>();
            value.iterator().forEachRemaining(attrList::add);
            out.name("attributes").value(JSONSerializer.instance().serialize(attrList));
        out.endObject();
    }

    @Override
    public Material read(JsonReader in) throws IOException {
        Material mtl = new Material();
        in.beginObject();
        while(in.hasNext()) {
            switch(in.nextName()) {
                case "id": mtl.id = in.nextString(); break;
                case "attributes":
                    ArrayList<LinkedTreeMap> attrs = JSONSerializer.instance().deserialize(in.nextString(), ArrayList.class);
                    attrs.forEach(map -> {
                        Attribute attr = null;
                        String typeStr = map.get("type")+"";
                        typeStr = typeStr.substring(0, typeStr.indexOf('.'));
                        long type = Long.parseLong(typeStr);
                        if(map.containsKey("color")) {// COLOUR ATTRIBUTE
                            LinkedTreeMap col = (LinkedTreeMap) map.get("color");
                            attr = new ColorAttribute(type, buildColour(col));
                        }else if(map.containsKey("value")) {// BLENDING ATTRIBUTE
                            attr = new BlendingAttribute(((Double)map.get("value")).floatValue());
                        }else if(map.containsKey("textureDescription")) {// TEXTURE ATTRIBUTE
                            attr = new TextureAttribute(type, buildDescriptor((LinkedTreeMap) map.get("textureDescription")));
                        }
                        if(attr != null) mtl.set(attr);
                    });
                    break;
            }
        }
        in.endObject();
        return null;
    }

    private TextureDescriptor<Texture> buildDescriptor(LinkedTreeMap map) {
        TextureDescriptor<Texture> desc = new TextureDescriptor<>();
        if(map.containsKey("textureRef")) desc.texture = Argent.content.get(map.get("textureRef").toString(), Texture.class);
        if(map.containsKey("minFilter"))  desc.minFilter = Texture.TextureFilter.valueOf(map.get("minFilter").toString());
        if(map.containsKey("magFilter"))  desc.magFilter = Texture.TextureFilter.valueOf(map.get("magFilter").toString());
        if(map.containsKey("uWrap"))      desc.uWrap = Texture.TextureWrap.valueOf(map.get("uWrap").toString());
        if(map.containsKey("vWrap"))      desc.vWrap = Texture.TextureWrap.valueOf(map.get("vWrap").toString());
        return desc;
    }

    private Color buildColour(LinkedTreeMap map) {
        Color col = new Color();
        if(map.containsKey("r")) col.r = ((Double)map.get("r")).floatValue();
        if(map.containsKey("g")) col.g = ((Double)map.get("g")).floatValue();
        if(map.containsKey("b")) col.b = ((Double)map.get("b")).floatValue();
        if(map.containsKey("a")) col.a = ((Double)map.get("a")).floatValue();
        return col;
    }
}