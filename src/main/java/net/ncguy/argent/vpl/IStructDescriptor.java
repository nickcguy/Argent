package net.ncguy.argent.vpl;

import com.badlogic.gdx.graphics.Color;

/**
 * Created by Guy on 18/08/2016.
 */
public interface IStructDescriptor<T, U> {

    Color colour();
    T makeStruct(U[] args);
    U breakStruct(T t, int pinId);

    int outPins();

}
