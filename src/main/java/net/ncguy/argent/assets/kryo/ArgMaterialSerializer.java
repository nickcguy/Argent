package net.ncguy.argent.assets.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import net.ncguy.argent.assets.ArgMaterial;

/**
 * Created by Guy on 01/08/2016.
 */
public class ArgMaterialSerializer extends Serializer<ArgMaterial> {

    @Override
    public void write(Kryo kryo, Output output, ArgMaterial object) {

    }

    @Override
    public ArgMaterial read(Kryo kryo, Input input, Class<ArgMaterial> type) {
        return null;
    }
}
