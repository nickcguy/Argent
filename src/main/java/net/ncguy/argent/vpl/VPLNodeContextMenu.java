package net.ncguy.argent.vpl;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Selection;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.PopupMenu;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Guy on 22/08/2016.
 */
public class VPLNodeContextMenu extends PopupMenu {

    VPLGraph graph;

    public VPLNodeContextMenu(VPLGraph graph) {
        super();
        this.graph = graph;
        init();
    }

    public void init() {
        MenuItem fire = new MenuItem("Fire Node", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                VPLNode node = graph.nodeSelection.first();
                if(node == null) {
                    graph.toaster.info("No node selected");
                    return;
                }
                try {
                    node.invokeSelf(0);
                } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException | InstantiationException e) {
                    e.printStackTrace();
                }
            }
        });
        MenuItem continuous = new MenuItem("Toggle continuous", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                VPLNode node = graph.nodeSelection.first();
                if(node == null) {
                    graph.toaster.info("No node selected");
                    return;
                }
                node.continuous = !node.continuous;
            }
        });
        MenuItem delete = new MenuItem("Delete", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Selection<VPLNode> nodes = graph.nodeSelection;
                if(nodes == null || nodes.size() == 0) {
                    graph.toaster.info("No node selected");
                    return;
                }
                nodes.forEach(graph::removeNode);
                nodes.clear();
            }
        });

        MenuItem toFront = new MenuItem("Bring to front", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Selection<VPLNode> nodes = graph.nodeSelection;
                if(nodes == null || nodes.size() == 0) {
                    graph.toaster.info("No node selected");
                    return;
                }
                nodes.forEach(VPLNode::toFront);
            }
        });
        MenuItem toBack = new MenuItem("Send to back", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Selection<VPLNode> nodes = graph.nodeSelection;
                if(nodes == null || nodes.size() == 0) {
                    graph.toaster.info("No node selected");
                    return;
                }
                nodes.forEach(VPLNode::toBack);
            }
        });

        addItem(fire);
        addItem(continuous);
        addSeparator();
        addItem(delete);
        addSeparator();
        addItem(toFront);
        addItem(toBack);
    }

}
