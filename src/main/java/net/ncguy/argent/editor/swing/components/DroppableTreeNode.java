package net.ncguy.argent.editor.swing.components;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Created by Guy on 27/06/2016.
 */
public class DroppableTreeNode extends DefaultMutableTreeNode {

    public DroppableTreeNode() { super(); init(); }
    public DroppableTreeNode(Object userObject) { super(userObject); init(); }
    public DroppableTreeNode(Object userObject, boolean allowsChildren) { super(userObject, allowsChildren); init(); }

    protected void init() {}


}
