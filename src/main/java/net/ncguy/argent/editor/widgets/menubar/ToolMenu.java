package net.ncguy.argent.editor.widgets.menubar;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuItem;
import net.ncguy.argent.editor.EditorUI;
import net.ncguy.argent.editor.project.ProjectManager;
import net.ncguy.argent.editor.tools.ToolManager;
import net.ncguy.argent.injector.ArgentInjector;
import net.ncguy.argent.injector.Inject;

/**
 * Created by Guy on 30/07/2016.
 */
public class ToolMenu extends Menu {

    private MenuItem defaultTool;
    private MenuItem translateTool;
    private MenuItem rotateTool;
    private MenuItem scaleTool;

    @Inject
    private ToolManager toolManager;
    @Inject
    private ProjectManager projectManager;

    private EditorUI editorUI;

    public ToolMenu(EditorUI editorUI) {
        super("Tools");
        this.editorUI = editorUI;
        ArgentInjector.inject(this);

        defaultTool = new MenuItem("Default Tool");
        translateTool = new MenuItem("Translate Tool");
        rotateTool = new MenuItem("Rotate Tool");
        scaleTool = new MenuItem("Scale Tool");

        addItem(defaultTool);
        addItem(translateTool);
        addItem(rotateTool);
        addItem(scaleTool);

        setupListeners();
    }

    private void setupListeners() {
        defaultTool.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toolManager.setDefaultTool();
            }
        });
        translateTool.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toolManager.activateTool(toolManager.translateTool);
            }
        });
        rotateTool.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toolManager.activateTool(toolManager.rotateTool);
            }
        });
        scaleTool.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toolManager.activateTool(toolManager.scaleTool);
            }
        });
    }

}
