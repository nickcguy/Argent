package net.ncguy.argent.vpl.struct.bridge;

/**
 * Created by Guy on 22/08/2016.
 */
public interface IStructBridge<T, U> {

    default boolean biDirectional() { return false; }
    default T reverse(U u) { return null; }

    U bridge(T t);

}
