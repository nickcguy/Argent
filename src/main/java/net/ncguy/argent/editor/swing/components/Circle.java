package net.ncguy.argent.editor.swing.components;

import net.ncguy.argent.utils.HSBColour;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Guy on 27/06/2016.
 */
public class Circle extends JComponent implements IColourable {

    @Override
    public void paint(Graphics g) {
        g.setColor(colour());
        int diameter = getHeight();
        int y = getY();
        if(getHeight() > getWidth()) {
            diameter = getWidth();
        }
        g.fillOval(getX(), y, diameter, diameter);
        g.setColor(Color.WHITE);
    }

    private Color colour;

    @Override
    public void colour(float h, float s, float b) {
        this.colour = Color.getHSBColor(h, s, b);
        repaint();
    }

    @Override
    public Color colour() {
        if(this.colour == null) this.colour = Color.WHITE;
        return this.colour;
    }

    @Override
    public HSBColour hsb() {
        return HSBColour.fromRGB(colour());
    }
}
