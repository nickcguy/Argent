package net.ncguy.argent.io.serialization;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.ncguy.argent.core.VarRunnables;
import net.ncguy.argent.io.serialization.adapter.MaterialTypeAdapter;
import net.ncguy.argent.io.serialization.adapter.TextureDescriptorTypeAdapter;
import net.ncguy.argent.io.serialization.adapter.factory.MaterialTypeAdapterFactory;
import net.ncguy.argent.io.serialization.adapter.factory.TextureDescriptorTypeAdapterFactory;

/**
 * Created by Guy on 09/06/2016.
 */
public class JSONSerializer implements ISerializer {

    private static JSONSerializer instance;

    public static JSONSerializer instance() {
        if(instance == null)
            instance = new JSONSerializer();
        return instance;
    }

    private Gson gson;

    private JSONSerializer() {
        GsonBuilder gb = new GsonBuilder();
        // TextureDescriptor
        gb.registerTypeAdapter(TextureDescriptor.class, new TextureDescriptorTypeAdapter());
        gb.registerTypeAdapterFactory(new TextureDescriptorTypeAdapterFactory());
        // Material
        gb.registerTypeAdapter(Material.class, new MaterialTypeAdapter());
        gb.registerTypeAdapterFactory(new MaterialTypeAdapterFactory());
//        gb.registerTypeAdapter(DynamicShader.Info.class, new DynamicShaderInfoTypeAdapter());
        gb.setPrettyPrinting();
        gson = gb.create();
    }


    @Override
    public String serialize(Object obj, VarRunnables.VarRunnable callback) {
        String json = gson.toJson(obj);
        if(callback != null) callback.run(json);
        return json;
    }

    @Override
    public <T> T deserialize(String json, Class<T> cls) {
        try {
            T obj = gson.fromJson(json, cls);
            return obj;
        }catch (Exception jse) {
            jse.printStackTrace();
        }
        return null;
    }

}
