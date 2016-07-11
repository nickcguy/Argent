package net.ncguy.argent.editor.lwjgl.app.panel;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import net.ncguy.argent.editor.ConfigurableAttribute;
import net.ncguy.argent.editor.IConfigurable;
import net.ncguy.argent.editor.shared.config.builders.AbstractComponentBuilder;
import net.ncguy.argent.editor.shared.config.builders.GDXComponentBuilder;
import net.ncguy.argent.world.GameWorld;

import java.util.List;

/**
 * Created by Guy on 04/07/2016.
 */
public class DataPanel<T> extends AbstractPanel<T> {

    protected Tree configTree;
    protected AbstractComponentBuilder builder = GDXComponentBuilder.instance();
    protected ScrollPane configScroller;

    public DataPanel(Stage stage, GameWorld.Generic<T> gameWorld) {
        super(stage, gameWorld);
    }

    @Override
    protected AbstractPanel ui() {
        configTree = new Tree(skin);
        configScroller = new ScrollPane(configTree, skin);
        addActor(configScroller);
        return this;
    }

    @Override
    protected AbstractPanel listeners() {
        return this;
    }

    @Override
    protected AbstractPanel select(T obj) {
        gameWorld.select(obj);
        configTree.clearChildren();
        if(obj instanceof IConfigurable) {
            List<ConfigurableAttribute<?>> attrs = ((IConfigurable) obj).getConfigAttrs();
            builder.compileSet(configTree, attrs);
        }
        configTree.getRootNodes().forEach(node -> node.setExpanded(true));
        return this;
    }



    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        resizeElements();
    }

    protected void resizeElements() {
        if(configScroller != null) {
            configScroller.setBounds(0, 0, getWidth(), getHeight());
        }

        if(configTree != null) {
            configTree.pack();
            configTree.setX(5);
            configTree.setWidth(configScroller.getWidth()-10);
            configTree.setY(configTree.getHeight());
//            reselect();
        }
    }



}
