package net.ncguy.argent.io.serialization.adapter.factory;

import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import net.ncguy.argent.io.serialization.adapter.TextureDescriptorTypeAdapter;

/**
 * Created by Guy on 11/07/2016.
 */
public class TextureDescriptorTypeAdapterFactory implements TypeAdapterFactory {

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if(!TextureDescriptor.class.isAssignableFrom(type.getRawType()))
            return null;
        return (TypeAdapter<T>) new TextureDescriptorTypeAdapter();
    }

}
