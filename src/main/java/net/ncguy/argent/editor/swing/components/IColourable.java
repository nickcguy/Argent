package net.ncguy.argent.editor.swing.components;

import net.ncguy.argent.utils.HSBColour;

import java.awt.*;

/**
 * Created by Guy on 27/06/2016.
 */
public interface IColourable {

    void colour(float h, float s, float b);
    Color colour();
    HSBColour hsb();

}
