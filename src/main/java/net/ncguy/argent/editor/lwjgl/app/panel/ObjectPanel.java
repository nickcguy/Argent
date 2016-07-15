package net.ncguy.argent.editor.lwjgl.app.panel;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.SnapshotArray;
import com.kotcrab.vis.ui.VisUI;
import net.ncguy.argent.ui.ArgentList;
import net.ncguy.argent.world.GameWorld;

import java.util.ArrayList;

/**
 * Created by Guy on 09/07/2016.
 */
public class ObjectPanel<T> extends RootPanel<T> {

    private ScrollPane scroller;
    private ArgentList<T> objList;

    public ObjectPanel(Stage stage, GameWorld.Generic<T> gameWorld) {
        super(stage, gameWorld);
    }

    @Override
    protected void init() {
        panels = new ArrayList<>();
        panels.add(new DataPanel<>(stage, gameWorld));
        panels.add(new MaterialPanel<>(stage, gameWorld));
        ui().listeners();
        sizeChanged();
    }

    @Override
    protected ObjectPanel ui() {
        objList = new ArgentList<>(VisUI.getSkin());
        scroller = new ScrollPane(objList);
        addActor(scroller);
        populateList();
        super.ui();
        return this;
    }

    @Override
    protected ObjectPanel listeners() {
        objList.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameWorld.select(objList.getSelected().obj);
            }
        });
        super.listeners();
        return this;
    }

    public void populateList() {
        objList.getRawItems().forEach(i -> {
            if(!gameWorld.instances().contains(i))
                objList.removeItem(i);
        });
        ObjectPanel<T> that = this;
        gameWorld.instances().forEach(i -> {
            if(!objList.containsItem(i))
                objList.addItem(i, that::select);
        });
    }



    @Override
    protected void resizeElements() {
        if(scroller != null) {
            scroller.setBounds(0, 0, getWidth()*0.15f, getHeight());
            scroller.setScrollBarPositions(true, true);
        }
        if(objList != null) {
            objList.pack();
            objList.setPosition(0, 0);
            objList.setWidth(scroller.getWidth()-30);
        }
        Table table = tabControl.getTable();
        table.setSize(getWidth()*0.85f, 24);
        table.setPosition(getWidth()*0.15f, getHeight()-table.getHeight());

        activePane.setBounds(getWidth()*0.15f, 0, getWidth()*0.85f, getHeight()-table.getHeight());
        SnapshotArray<Actor> paneChildren = activePane.getChildren();
        if(paneChildren.size >= 1) {
            paneChildren.first().setBounds(0, 0, activePane.getWidth(), activePane.getHeight());
        }
    }
}
