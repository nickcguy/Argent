package net.ncguy.argent.editor.widgets.sidebar;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.PopupMenu;
import net.ncguy.argent.Argent;
import net.ncguy.argent.editor.EditorUI;
import net.ncguy.argent.editor.project.ProjectContext;
import net.ncguy.argent.editor.project.ProjectManager;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.event.SceneGraphChangedEvent;
import net.ncguy.argent.injector.ArgentInjector;
import net.ncguy.argent.injector.Inject;

/**
 * Created by Guy on 29/07/2016.
 */
public class ContextMenu extends PopupMenu {

    private MenuItem addEmpty;
    private MenuItem addTerrain;
    private MenuItem duplicate;
    private MenuItem delete;

    private WorldEntity selected;

    @Inject
    private ProjectManager projectManager;

    private EditorUI editorUI;

    public ContextMenu(EditorUI editorUI) {
        super();
        this.editorUI = editorUI;
        ArgentInjector.inject(this);
        final ProjectContext context = projectManager.current();
        addEmpty = new MenuItem("Add Empty");
        addTerrain = new MenuItem("Add Terrain");
        duplicate = new MenuItem("Duplicate");
        delete = new MenuItem("Delete");

        addEmpty.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                context.currScene.sceneGraph.addWorldEntity(new WorldEntity());
                Argent.event.post(new SceneGraphChangedEvent());
            }
        });
        delete.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(selected != null) {
                    context.currScene.sceneGraph.removeWorldEntity(selected);
                    selected = null;
                }
                Argent.event.post(new SceneGraphChangedEvent());
            }
        });

        // TODO implement "addTerrain" and "duplicate" menu items

        addTerrain.setDisabled(true);
        duplicate.setDisabled(true);

        addItem(addEmpty);
        addItem(addTerrain);
        addItem(duplicate);
        addItem(delete);
    }

    public void show(WorldEntity entity, float x, float y) {
        selected = entity;

        delete.setDisabled(selected == null);

        showMenu(editorUI, x, y);
    }

}
