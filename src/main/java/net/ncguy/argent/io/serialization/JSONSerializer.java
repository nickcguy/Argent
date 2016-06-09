package net.ncguy.argent.io.serialization;

import com.google.gson.Gson;

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
        gson = new Gson();
    }

    @Override
    public String serialize(Object obj) {
        return gson.toJson(obj);
    }

    @Override
    public <T> T deserialize(String json, Class<T> cls) {
        return gson.fromJson(json, cls);
    }

}
