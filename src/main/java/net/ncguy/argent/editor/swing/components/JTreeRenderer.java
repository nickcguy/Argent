package net.ncguy.argent.editor.swing.components;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

/**
 * Created by Guy on 27/06/2016.
 */
public class JTreeRenderer extends DefaultTreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        if(value instanceof IColourable) {
            IColourable colourable = (IColourable)value;
            setForeground(colourable.colour());
        }
        return this;
    }

}
