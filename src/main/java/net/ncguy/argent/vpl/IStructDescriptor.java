package net.ncguy.argent.vpl;

/**
 * Created by Guy on 18/08/2016.
 */
public interface IStructDescriptor<T, U> {

    T makeStruct(U[] args);
    U breakStruct(int pinId);

}
