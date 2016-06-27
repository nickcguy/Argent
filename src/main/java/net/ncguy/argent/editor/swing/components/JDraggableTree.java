package net.ncguy.argent.editor.swing.components;

import net.ncguy.argent.core.VarRunnables;
import net.ncguy.argent.render.shader.DynamicShader;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

/**
 * Created by Guy on 27/06/2016.
 */
public class JDraggableTree extends JTree {

    private void init() {
        setDragEnabled(true);
        setDropMode(DropMode.ON_OR_INSERT);
//        setCellRenderer(new JTreeRenderer());
        setTransferHandler(new TreeTransferHandler<DraggableTreeNode>((node) -> node instanceof DraggableTreeNode, (node) -> node instanceof DroppableTreeNode){
            @Override
            public DraggableTreeNode copy(TreeNode node) {
                DraggableTreeNode draggableTreeNode;
                if(node instanceof DefaultMutableTreeNode) {
                    Object obj = ((DefaultMutableTreeNode) node).getUserObject();
                    draggableTreeNode = new DraggableTreeNode(obj);
                    if(obj instanceof DynamicShader.Info)
                        ((DynamicShader.Info) obj).treeNode = draggableTreeNode;
                }else{
                    draggableTreeNode = new DraggableTreeNode(node);
                }
                return draggableTreeNode;
            }
        });
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    }

    public JDraggableTree() {
        super();
        init();
    }

    public JDraggableTree(Object[] value) {
        super(value);
        init();
    }

    public JDraggableTree(Vector<?> value) {
        super(value);
        init();
    }

    public JDraggableTree(Hashtable<?, ?> value) {
        super(value);
        init();
    }

    public JDraggableTree(TreeNode root) {
        super(root);
        init();
    }

    public JDraggableTree(TreeNode root, boolean asksAllowsChildren) {
        super(root, asksAllowsChildren);
        init();
    }

    public JDraggableTree(TreeModel newModel) {
        super(newModel);
        init();
    }


    public static abstract class TreeTransferHandler<T extends DefaultMutableTreeNode> extends TransferHandler {
        DataFlavor nodesFlavour;
        DataFlavor[] flavours = new DataFlavor[1];
        DefaultMutableTreeNode[] nodesToRemove;
        VarRunnables.VarReturnRunnable<TreeNode, Boolean> dragFilter, dropFilter;

        public TreeTransferHandler() {
            this(null, null);
        }

        public TreeTransferHandler(VarRunnables.VarReturnRunnable<TreeNode, Boolean> dragFilter) {
            this(dragFilter, null);
        }

        public TreeTransferHandler(VarRunnables.VarReturnRunnable<TreeNode, Boolean> dragFilter, VarRunnables.VarReturnRunnable<TreeNode, Boolean> dropFilter) {
            this.dragFilter = dragFilter;
            this.dropFilter = dropFilter;
            try {
                String mimeType = DataFlavor.javaJVMLocalObjectMimeType+";class=\""+DefaultMutableTreeNode[].class.getName()+"\"";
                nodesFlavour = new DataFlavor(mimeType);
                flavours[0] = nodesFlavour;
            }catch (ClassNotFoundException cnfe) {
                cnfe.printStackTrace();
            }
        }

        @Override
        public boolean canImport(TransferSupport support) {
            if(!support.isDrop()) return false;
            support.setShowDropLocation(true);
            if(!support.isDataFlavorSupported(nodesFlavour)) return false;

            JTree.DropLocation dl = (JTree.DropLocation)support.getDropLocation();
            JTree tree = (JTree)support.getComponent();

            int dropRow = tree.getRowForPath(dl.getPath());
            int[] selRows = tree.getSelectionRows();
            for(int i = 0; i < selRows.length; i++) {
                if(selRows[i] == dropRow)
                    return false;
            }

            int action = support.getDropAction();
            if(action == MOVE)
                return haveCompleteNode(tree);

            TreePath dest = dl.getPath();
            DefaultMutableTreeNode target = (DefaultMutableTreeNode)dest.getLastPathComponent();
            TreePath path = tree.getPathForRow(selRows[0]);
            DefaultMutableTreeNode firstNode = (DefaultMutableTreeNode)path.getLastPathComponent();

            if(dragFilter != null) if(!dragFilter.run(firstNode)) return false;
            if(dropFilter != null) if(!dropFilter.run(target)) return false;

            if(firstNode.getChildCount() > 0 && target.getLevel() < firstNode.getLevel())
                return false;

            return true;
        }

        private boolean haveCompleteNode(JTree tree) {
            int[] selRows = tree.getSelectionRows();
            TreePath path = tree.getPathForRow(selRows[0]);
            DefaultMutableTreeNode first = (DefaultMutableTreeNode)path.getLastPathComponent();

            if(dragFilter != null) if(!dragFilter.run(first)) return false;

            int childCount = first.getChildCount();

            if(childCount > 0 && selRows.length == 1)
                return false;

            for(int i = 1; i < selRows.length; i++) {
                path = tree.getPathForRow(selRows[i]);
                DefaultMutableTreeNode next = (DefaultMutableTreeNode)path.getLastPathComponent();
                if(first.isNodeChild(next))
                    if(childCount > selRows.length-1)
                        return false;
            }
            return true;
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            JTree tree = (JTree)c;
            TreePath[] paths = tree.getSelectionPaths();
            if(paths != null) {
                List<DefaultMutableTreeNode> copies = new ArrayList<>();
                List<DefaultMutableTreeNode> toRemove = new ArrayList<>();
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)paths[0].getLastPathComponent();

                if(dragFilter != null) if(!dragFilter.run(node)) return null;

                DefaultMutableTreeNode copy = copy(node);
                copies.add(copy);
                toRemove.add(node);
                for(int i = 1; i < paths.length; i++) {
                    DefaultMutableTreeNode next = (DefaultMutableTreeNode)paths[i].getLastPathComponent();
                    if(next.getLevel() < node.getLevel())
                        break;
                    else if(next.getLevel() > node.getLevel())
                        copy.add(copy(node));
                    else{
                        copies.add(copy(next));
                        toRemove.add(next);
                    }
                }
                DefaultMutableTreeNode[] nodes = copies.toArray(new DefaultMutableTreeNode[copies.size()]);
                nodesToRemove = toRemove.toArray(new DefaultMutableTreeNode[toRemove.size()]);
                return new NodesTransferable(nodes);
            }
            return null;
        }

        public abstract T copy(TreeNode node);

        @Override
        protected void exportDone(JComponent source, Transferable data, int action) {
            if((action & MOVE) == MOVE) {
                JTree tree = (JTree)source;
                DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
                for(int i = 0; i < nodesToRemove.length; i++)
                    model.removeNodeFromParent(nodesToRemove[i]);
            }
        }

        @Override
        public int getSourceActions(JComponent c) {
            return COPY_OR_MOVE;
        }

        @Override
        public boolean importData(TransferSupport support) {
            if(!canImport(support)) return false;
            DefaultMutableTreeNode[] nodes = null;
            try{
                Transferable t = support.getTransferable();
                nodes = (DefaultMutableTreeNode[])t.getTransferData(nodesFlavour);
            }catch (UnsupportedFlavorException | IOException e) {
                e.printStackTrace();
            }

            JTree.DropLocation dl = (JTree.DropLocation)support.getDropLocation();
            int childIndex = dl.getChildIndex();
            TreePath dest = dl.getPath();
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode)dest.getLastPathComponent();

            if(dropFilter != null) if(!dropFilter.run(parent)) return false;

            JTree tree = (JTree)support.getComponent();
            DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
            int index = childIndex;
            if(childIndex == -1)
                index = parent.getChildCount();
            for (DefaultMutableTreeNode node : nodes)
                model.insertNodeInto(node, parent, index++);
            return true;

        }

        public class NodesTransferable implements Transferable {
            DefaultMutableTreeNode[] nodes;

            public NodesTransferable(DefaultMutableTreeNode[] nodes) {
                this.nodes = nodes;
            }

            @Override
            public DataFlavor[] getTransferDataFlavors() {
                return flavours;
            }

            @Override
            public boolean isDataFlavorSupported(DataFlavor flavor) {
                return nodesFlavour.equals(flavor);
            }

            @Override
            public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
                if(!isDataFlavorSupported(flavor)) throw new UnsupportedFlavorException(flavor);
                return nodes;
            }
        }

    }

}
