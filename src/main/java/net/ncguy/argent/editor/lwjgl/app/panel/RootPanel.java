package net.ncguy.argent.editor.lwjgl.app.panel;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.SnapshotArray;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter;
import net.ncguy.argent.world.GameWorld;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guy on 04/07/2016.
 */
public class RootPanel<T> extends AbstractPanel<T> {

    TabbedPane tabControl;
    Group activePane;
    List<AbstractPanel<T>> panels;

    private boolean internalChange = false;

    public RootPanel(GameWorld.Generic<T> gameWorld) {
        super(gameWorld);
    }

    @Override
    protected void init() {
        panels = new ArrayList<>();
        panels.add(new ObjectPanel<>(gameWorld));
        panels.add(new ShaderBufferPanel<>(gameWorld));
        super.init();
        sizeChanged();
    }

    protected RootPanel ui() {

        activePane = new Group();
        addActor(activePane);
        tabControl = new TabbedPane();
        activePane.clearChildren();
        activePane.addActor(panels.get(0).packToTab().getTrueContent());
        panels.forEach(p -> tabControl.add(p.packToTab()));
        addActor(tabControl.getTable());
        resizeElements();
        return this;
    }
    protected RootPanel listeners() {
        tabControl.addListener(new TabbedPaneAdapter(){
            @Override
            public void switchedTab(Tab tab) {
                if(tab instanceof PackedPanel) {
                    PackedPanel pnl = (PackedPanel)tab;
                    activePane.clearChildren();
                    activePane.addActor(pnl.getTrueContent());
                    resizeElements();
                }
            }
        });
        return this;
    }



    public RootPanel<T> select(T obj) {
        panels.forEach(p -> p.select(obj));
        return this;
    }

    protected void resizeElements() {
        Vector2 screenSize = new Vector2(getWidth(), getHeight());
        Table table = tabControl.getTable();
        table.setSize(screenSize.x, 24);
        table.setPosition(0, screenSize.y-table.getHeight());

        activePane.setBounds(0, 0, screenSize.x, screenSize.y-table.getHeight());
        SnapshotArray<Actor> paneChildren = activePane.getChildren();
        if(paneChildren.size >= 1) {
            paneChildren.first().setBounds(0, 0, activePane.getWidth(), activePane.getHeight());
        }
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        resizeElements();
    }
}
