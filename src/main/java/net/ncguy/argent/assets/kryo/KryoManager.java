package net.ncguy.argent.assets.kryo;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import net.ncguy.argent.assets.kryo.attribute.BlendingAttributeSerializer;
import net.ncguy.argent.assets.kryo.attribute.ColourAttributeSerializer;
import net.ncguy.argent.assets.kryo.attribute.TextureAttributeSerializer;
import net.ncguy.argent.assets.kryo.attribute.descriptor.TextureDescriptorSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by Guy on 01/08/2016.
 */
public class KryoManager {

    private static KryoManager kryoManager;
    public static KryoManager kryoManager() {
        if(kryoManager == null)
            kryoManager = new KryoManager();
        return kryoManager;
    }

    private Kryo kryo;

    public KryoManager() {
        this.kryo = new Kryo();
        register();
    }

    private void register() {
//        kryo.register(ArgMaterial.class, new ArgMaterialSerializer());
        kryo.register(Material.class, new MaterialSerializer());
        kryo.register(BlendingAttribute.class, new BlendingAttributeSerializer());
        kryo.register(ColorAttribute.class, new ColourAttributeSerializer());
        kryo.register(TextureAttribute.class, new TextureAttributeSerializer());
        kryo.register(TextureDescriptor.class, new TextureDescriptorSerializer());
    }

    public <T> T load(File file, Class<T> cls) throws FileNotFoundException {
        Input input = new Input(new FileInputStream(file));
        T t = kryo.readObject(input, cls);
        input.close();
        return t;
    }

    public <T> void save(File file, T object) throws FileNotFoundException {
        Output output = new Output(new FileOutputStream(file));
        kryo.writeObject(output, object);
        output.flush();
        output.close();
    }
}
