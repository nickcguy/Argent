package net.ncguy.argent.io.serialization.adapter.factory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.ncguy.argent.io.serialization.JSONSerializer;

import java.io.IOException;

/**
 * Created by Guy on 13/07/2016.
 */
public class AttributeTypeAdapter extends TypeAdapter<Attribute> {

    @Override
    public void write(JsonWriter out, Attribute value) throws IOException {
        if(value == null) {
            out.nullValue();
            return;
        }
        out.beginObject();
        String alias = value.getClass().getSimpleName();
        out.name("type").value(value.type);

        alias += "::" + value.getAttributeAlias(value.type);
        out.name("alias").value(alias);


        out.name("attrData").beginObject();
        if(value instanceof TextureAttribute) {
            out.name("offsetU").value(((TextureAttribute) value).offsetU);
            out.name("offsetV").value(((TextureAttribute) value).offsetV);
            out.name("scaleU").value(((TextureAttribute) value).scaleU);
            out.name("scaleV").value(((TextureAttribute) value).scaleV);
            out.name("textureDescriptor").value(JSONSerializer.instance().serialize(((TextureAttribute) value).textureDescription));
        }else if(value instanceof ColorAttribute) {
            out.name("colour").value(((ColorAttribute) value).color.toString());
        }else if(value instanceof BlendingAttribute) {
            out.name("alpha").value(((BlendingAttribute) value).opacity);
        }
        out.endObject();
        out.endObject();
    }

    @Override
    public Attribute read(JsonReader in) throws IOException {
        Attribute attr = null;
        long type = -1;
        String alias = "";
        in.beginObject();
        while(in.hasNext()) {
            switch(in.nextName()) {
                case "type": type = in.nextLong(); break;
                case "alias": alias = in.nextString(); break;
                case "attrData":
                    attr = buildFromAlias(alias);
                    if(attr == null) break;
                    in.beginObject();
                    while(in.hasNext()) {
                        switch(in.nextName()) {
                            case "offsetU":
                                ((TextureAttribute)attr).offsetU = (float) in.nextDouble(); break;
                            case "offsetV":
                                ((TextureAttribute)attr).offsetV = (float) in.nextDouble(); break;
                            case "scaleU":
                                ((TextureAttribute)attr).scaleU = (float) in.nextDouble(); break;
                            case "scaleV":
                                ((TextureAttribute)attr).scaleV = (float) in.nextDouble(); break;
                            case "textureDescriptor":
                                ((TextureAttribute)attr).textureDescription.set(JSONSerializer.instance().deserialize(in.nextString(), TextureDescriptor.class)); break;
                            case "colour":
                                ((ColorAttribute)attr).color.set(Color.valueOf(in.nextString())); break;
                            case "alpha":
                                ((BlendingAttribute)attr).opacity = (float) in.nextDouble(); break;
                        }
                    }
                    in.endObject();
                    break;
            }
        }
        in.endObject();
        return attr;
    }

    private Attribute buildFromAlias(String alias) {
        String[] aliasSegments = alias.split("::");
        Attribute attr = null;
        switch(aliasSegments[0]) {
            case "TextureAttribute":
                attr = new TextureAttribute(TextureAttribute.getAttributeType(aliasSegments[1]));
                break;
            case "BlendingAttribute":
                attr = new BlendingAttribute();
                break;
            case "ColorAttribute":
                attr = new ColorAttribute(ColorAttribute.getAttributeType(aliasSegments[1]));
                break;
        }
        return attr;
    }

    /*
    public final TextureDescriptor<Texture> textureDescription;
	public float offsetU = 0;
	public float offsetV = 0;
	public float scaleU = 1;
	public float scaleV = 1;
     */

}
