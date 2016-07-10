package net.ncguy.argent.editor.lwjgl.app.panel;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import net.ncguy.argent.editor.ConfigurableAttribute;
import net.ncguy.argent.editor.IConfigurable;
import net.ncguy.argent.editor.swing.config.descriptors.builders.AbstractComponentBuilder;
import net.ncguy.argent.editor.swing.config.descriptors.builders.GDXComponentBuilder;
import net.ncguy.argent.world.GameWorld;

import java.util.List;

/**
 * Created by Guy on 04/07/2016.
 */
public class DataPanel<T> extends AbstractPanel<T> {

    protected Table configTable;
    protected AbstractComponentBuilder builder = GDXComponentBuilder.instance();
    protected ScrollPane configScroller;

    public DataPanel(GameWorld.Generic<T> gameWorld) {
        super(gameWorld);
    }

    @Override
    protected AbstractPanel ui() {
        configTable = new Table();
        configTable.defaults().spaceBottom(2);
        configScroller = new ScrollPane(configTable, skin);
        addActor(configScroller);
        return this;
    }

    @Override
    protected AbstractPanel listeners() {
        return this;
    }

    @Override
    protected AbstractPanel select(T obj) {
        configTable.clearChildren();
        if(obj instanceof IConfigurable) {
            List<ConfigurableAttribute<?>> attrs = ((IConfigurable) obj).getConfigAttrs();
            attrs.forEach(ca -> {
                System.out.println(ca.displayName());
                Object compObj = builder.buildComponent(ca);
                if (compObj instanceof Actor) {
                    float compWidth = getWidth()-192;
                    configTable.add(new Label(ca.displayName(), skin)).width(192);
                    configTable.add((Actor) compObj).width(compWidth-10);
                    configTable.row();
                }
            });
        }
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

        if(configTable != null) {
            configTable.pack();
            configTable.setX(5);
            configTable.setWidth(configScroller.getWidth()-10);
            configTable.setY(configTable.getHeight());
            reselect();
        }
    }



}
