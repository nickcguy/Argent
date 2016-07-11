package net.ncguy.argent.io.serialization.adapter.factory;

import com.badlogic.gdx.graphics.g3d.Material;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import net.ncguy.argent.io.serialization.adapter.MaterialTypeAdapter;

/**
 * Created by Guy on 11/07/2016.
 */
public class MaterialTypeAdapterFactory implements TypeAdapterFactory {

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if(!Material.class.isAssignableFrom(type.getRawType()))
            return null;
        return (TypeAdapter<T>) new MaterialTypeAdapter();
    }

}
