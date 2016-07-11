package net.ncguy.argent.editor.lwjgl.app.panel;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import net.ncguy.argent.world.GameWorld;

/**
 * Created by Guy on 04/07/2016.
 */
public abstract class AbstractPanel<T> extends Group {

    protected static Skin skin;
    protected Stage stage;
    protected GameWorld.Generic<T> gameWorld;

    public AbstractPanel(Stage stage, GameWorld.Generic<T> gameWorld) {
        this.stage = stage;
        this.gameWorld = gameWorld;
        skin = VisUI.getSkin();
        init();
    }

    protected void init() {
        ui().listeners();
    }

    protected String name() {
        return this.getClass().getSimpleName().replace("Panel", "");
    }

    protected abstract AbstractPanel ui();
    protected abstract AbstractPanel listeners();
    protected abstract AbstractPanel select(T obj);

    protected AbstractPanel reselect() {
        return select(selected());
    }

    public T selected() {
        return gameWorld.selected();
    }

    public PackedPanel packToTab() {
        return packToTab(name());
    }
    public PackedPanel packToTab(String name) {
        return new PackedPanel(name, this);
    }

    public static class PackedPanel extends Tab {

        private String name;
        private AbstractPanel panel;

        public PackedPanel(String name, AbstractPanel panel) {
            super(false, false);
            this.name = name;
            this.panel = panel;
        }

        @Override
        public String getTabTitle() {
            return name;
        }

        @Override
        public Table getContentTable() {
            Table t = new Table();
            t.addActor(panel);
            return t;
        }

        public Group getTrueContent() {
            return panel;
        }

        public Class<?> getTrueClass() {
            return panel.getClass();
        }
    }

}
