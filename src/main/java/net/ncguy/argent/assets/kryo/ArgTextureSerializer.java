package net.ncguy.argent.assets.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import net.ncguy.argent.assets.ArgTexture;
import net.ncguy.argent.editor.project.ProjectManager;
import net.ncguy.argent.injector.ArgentInjector;
import net.ncguy.argent.injector.Inject;
import net.ncguy.argent.utils.TextureCache;

/**
 * Created by Guy on 05/09/2016.
 */
public class ArgTextureSerializer extends Serializer<ArgTexture> {

    @Inject
    ProjectManager manager;

    @Override
    public void write(Kryo kryo, Output out, ArgTexture obj) {
        String name = obj.name();
        String path = obj.getPath();
        kryo.writeObject(out, name != null ? name : "");
        kryo.writeObject(out, path != null ? path : "");
    }

    @Override
    public ArgTexture read(Kryo kryo, Input input, Class<ArgTexture> type) {
        if(manager == null)
            ArgentInjector.inject(this);

        String name = kryo.readObject(input, String.class);
        String path = kryo.readObject(input, String.class);

        for (ArgTexture tex : manager.current().textures()) {
            if(name.equalsIgnoreCase(tex.name()) && path.equalsIgnoreCase(tex.getPath()))
                return tex;
        }

        return new ArgTexture(TextureCache.white());
    }
}
