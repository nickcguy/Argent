package net.ncguy.argent.io.serialization;

/**
 * Created by Guy on 09/06/2016.
 */
public interface ISerializer {

    String serialize(Object obj);
    <T> T deserialize(String s, Class<T> cls);

}
