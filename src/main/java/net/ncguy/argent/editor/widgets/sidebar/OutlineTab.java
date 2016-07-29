package net.ncguy.argent.editor.widgets.sidebar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import net.ncguy.argent.Argent;
import net.ncguy.argent.editor.EditorUI;
import net.ncguy.argent.editor.project.ProjectManager;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.event.SceneGraphChangedEvent;
import net.ncguy.argent.event.WorldEntitySelectedEvent;
import net.ncguy.argent.injector.ArgentInjector;
import net.ncguy.argent.injector.Inject;
import net.ncguy.argent.render.scene.SceneGraph;

/**
 * Created by Guy on 29/07/2016.
 */
public class OutlineTab extends Tab implements WorldEntitySelectedEvent.WorldEntitySelectedListener, SceneGraphChangedEvent.SceneGraphChangedListener {

    private Table content;
    private List<WorldEntity> list;
    private ScrollPane scroller;

    private ContextMenu contextMenu;

    @Inject
    private ProjectManager projectManager;
    private EditorUI editorUI;

    public OutlineTab(EditorUI editorUI) {
        super(false, false);
        this.editorUI = editorUI;
        ArgentInjector.inject(this);
        Argent.event.register(this);

        contextMenu = new ContextMenu(editorUI);

        content = new Table(VisUI.getSkin());
        content.align(Align.left | Align.top);
        list = new List<>(VisUI.getSkin());
        list.getSelection().setProgrammaticChangeEvents(false);
        scroller = new ScrollPane(list, VisUI.getSkin());
        scroller.setFlickScroll(false);
        scroller.setFadeScrollBars(false);
        content.add(scroller).fill().expand();

        setupListeners();
    }

    private void setupListeners() {
        scroller.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                editorUI.setScrollFocus(scroller);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                editorUI.setScrollFocus(null);
            }
        });
        list.addListener(new InputListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if(button != Input.Buttons.RIGHT) return;
                Object obj = getObjAt(y);
                WorldEntity e = null;
                if(obj != null) {
                    if(obj instanceof WorldEntity) e = (WorldEntity)obj;
                }
                contextMenu.show(e, Gdx.input.getX(), Gdx.graphics.getHeight()-Gdx.input.getY());
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        list.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Object sel = list.getSelected();
                if(sel != null) {
                    WorldEntity e = (WorldEntity)sel;
                    projectManager.current().currScene.select(e);
                    Argent.event.post(new WorldEntitySelectedEvent(e));
                }
            }
        });
    }

    private void buildList(SceneGraph sceneGraph) {
        list.clearItems();
        sceneGraph.getWorldEntities().forEach(this::addToList);
    }

    private void addToList(WorldEntity e) {
        list.getItems().add(e);
    }

    private Object getObjAt(float y) {
        float h = list.getItemHeight();
        y = list.getHeight() - y;
        int index = (int) Math.floor(y/h);
        if(index >= list.getItems().size || index < 0) {
            return null;
        }
        return list.getItems().get(index);
    }

    @Override
    public String getTabTitle() {
        return "Outline";
    }

    @Override
    public Table getContentTable() {
        return content;
    }

    @Override
    public void onWorldEntitySelected(WorldEntitySelectedEvent event) {
        list.setSelected(event.getEntity());
    }

    @Override
    public void onSceneGraphChanged(SceneGraphChangedEvent sceneGraphChangedEvent) {
        buildList(projectManager.current().currScene.sceneGraph);
    }
}
