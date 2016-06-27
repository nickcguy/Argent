package net.ncguy.argent.editor.swing.components;

import net.ncguy.argent.utils.HSBColour;

import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

/**
 * Created by Guy on 27/06/2016.
 */
public class DraggableTreeNode extends DefaultMutableTreeNode implements IColourable {

    public DraggableTreeNode() { super(); init(); }
    public DraggableTreeNode(Object userObject) { super(userObject); init(); }
    public DraggableTreeNode(Object userObject, boolean allowsChildren) { super(userObject, allowsChildren); init(); }

    protected void init() {
        setAllowsChildren(false);
    }

    private Color colour;

    @Override
    public void colour(float h, float s, float b) {
        this.colour = Color.getHSBColor(h, s, b);
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
