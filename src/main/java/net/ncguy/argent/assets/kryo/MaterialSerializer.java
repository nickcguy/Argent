package net.ncguy.argent.assets.kryo;

import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.util.ArrayList;

/**
 * Created by Guy on 01/08/2016.
 */
public class MaterialSerializer extends Serializer<Material>  {

    @Override
    public void write(Kryo kryo, Output output, Material mtl) {
        output.writeString(mtl.id);
        output.writeInt(mtl.size());

        ArrayList<Attribute> attrList = new ArrayList<>();
        mtl.iterator().forEachRemaining(attrList::add);

        attrList.forEach(attribute -> {
            Class<?> cls = attribute.getClass();
            if(Attribute.class.isAssignableFrom(cls)) {
                Serializer<Attribute> s = kryo.getSerializer(cls);
                if (s == null) return;
                output.writeString(cls.getCanonicalName());
                s.write(kryo, output, attribute);
            }
        });
    }

    @Override
    public Material read(Kryo kryo, Input input, Class<Material> type) {
        Material mtl = new Material();
        mtl.id = input.readString();
        int size = input.readInt();
        for(int i = 0; i < size; i++) {
            String clsName = input.readString();
            try {
                Class<Attribute> cls = (Class<Attribute>) Class.forName(clsName);
                if(cls == null) continue;
                Serializer<Attribute> s = kryo.getSerializer(cls);
                if (s == null) continue;
                Attribute attr = s.read(kryo, input, cls);
                if(attr != null)
                    mtl.set(attr);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return mtl;
    }
}
