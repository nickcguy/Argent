package net.ncguy.argent.io.serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.ncguy.argent.core.VarRunnables;

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
//        gb.registerTypeAdapter(DynamicShader.Info.class, new DynamicShaderInfoTypeAdapter());
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
