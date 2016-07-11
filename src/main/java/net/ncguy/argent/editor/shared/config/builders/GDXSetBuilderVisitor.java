package net.ncguy.argent.editor.shared.config.builders;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import net.ncguy.argent.ui.LabeledActor;
import net.ncguy.argent.utils.data.tree.Visitable;
import net.ncguy.argent.utils.data.tree.Visitor;

/**
 * Created by Guy on 11/07/2016.
 */
public class GDXSetBuilderVisitor implements Visitor<GDXComponentBuilder.AttributeWrapper> {

    private Tree tree;
    private Tree.Node node;
    private Skin skin;
    private String cat;

    public GDXSetBuilderVisitor(Tree tree, Tree.Node node, Skin skin) {
        this(tree, node, skin, "root");
    }

    public GDXSetBuilderVisitor(Tree tree, Tree.Node node, Skin skin, String cat) {
        this.tree = tree;
        this.node = node;
        this.skin = skin;
        this.cat = cat;
    }

    @Override
    public Visitor<GDXComponentBuilder.AttributeWrapper> visitVisitable(Visitable<GDXComponentBuilder.AttributeWrapper> visitable) {
        Tree.Node node = new Tree.Node(new Label(visitable.data().name, skin));
        GDXComponentBuilder.AttributeWrapper data = visitable.data();
        if(data.attr != null) {
            Object compObj = GDXComponentBuilder.instance().buildComponent(data.attr);
            if(compObj instanceof Actor) {
                LabeledActor nodeActor = new LabeledActor(skin, data.name, (Actor)compObj);
                float compWidth = tree.getWidth() - 192;
                nodeActor.labelCell().width(192);
                nodeActor.actorCell().width(compWidth-50);
                node = new Tree.Node(nodeActor);
            }
        }
        this.node.add(node);
        return new GDXSetBuilderVisitor(tree, node, skin);
    }

    @Override
    public void visitData(Visitable<GDXComponentBuilder.AttributeWrapper> visitable, GDXComponentBuilder.AttributeWrapper data) {
        if(true) return;
        if(data.attr != null) {
            Object compObj = GDXComponentBuilder.instance().buildComponent(data.attr);
            if(compObj instanceof Actor) {
                Tree.Node node = new Tree.Node((Actor)compObj);
                this.node.add(node);
                float compWidth = tree.getWidth() - 192;
                ((Actor) compObj).setWidth(compWidth-30);
            }
        }
        cat = data.name+": "+data.path;
    }
}
