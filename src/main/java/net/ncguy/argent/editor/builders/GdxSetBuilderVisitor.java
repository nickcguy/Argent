package net.ncguy.argent.editor.builders;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.kotcrab.vis.ui.VisUI;
import net.ncguy.argent.data.config.ConfigurableAttribute;
import net.ncguy.argent.data.tree.TreeObjectWrapper;
import net.ncguy.argent.data.tree.Visitable;
import net.ncguy.argent.data.tree.Visitor;
import net.ncguy.argent.ui.LabeledActor;

/**
 * Created by Guy on 21/07/2016.
 */
public class GdxSetBuilderVisitor implements Visitor<TreeObjectWrapper<ConfigurableAttribute<?>>> {

    private static GDXComponentBuilder builder = GDXComponentBuilder.instance();
    private Tree tree;
    private Tree.Node node;

    public GdxSetBuilderVisitor(Tree tree, Tree.Node node) {
        this.tree = tree;
        this.node = node;
    }

    @Override
    public Visitor<TreeObjectWrapper<ConfigurableAttribute<?>>> visit(Visitable<TreeObjectWrapper<ConfigurableAttribute<?>>> visitable) {
        Tree.Node node = new Tree.Node(new Label(visitable.data().label, VisUI.getSkin()));
        TreeObjectWrapper<ConfigurableAttribute<?>> attr = visitable.data();
        if(attr.object != null) {
            Object compObj = builder.buildComponent(attr.object);
            if(compObj instanceof Actor) {
                LabeledActor nodeActor = new LabeledActor(attr.label, (Actor)compObj);
                float labelWidth = 128;
                float compPadding = 50;
                float compWidth = tree.getWidth() - labelWidth;
                nodeActor.labelCell().width(labelWidth);
                nodeActor.actorCell().width(compWidth - compPadding);
                node = new Tree.Node(nodeActor);
            }
        }
        this.node.add(node);
        return new GdxSetBuilderVisitor(this.tree, node);
    }

    @Override
    public void visitData(Visitable<TreeObjectWrapper<ConfigurableAttribute<?>>> visitable, TreeObjectWrapper<ConfigurableAttribute<?>> data) {

    }
}
