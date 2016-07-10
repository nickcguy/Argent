package net.ncguy.argent.io.serialization;

import net.ncguy.argent.core.VarRunnables;

/**
 * Created by Guy on 09/06/2016.
 */
public interface ISerializer {

    default String serialize(Object obj) {
        return serialize(obj, null);
    }

    String serialize(Object obj, VarRunnables.VarRunnable callback);
    <T> T deserialize(String s, Class<T> cls);

}
