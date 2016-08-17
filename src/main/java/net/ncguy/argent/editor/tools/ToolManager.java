package net.ncguy.argent.editor.tools;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import net.ncguy.argent.editor.CommandHistory;
import net.ncguy.argent.editor.project.ProjectManager;
import net.ncguy.argent.editor.tools.picker.ToolHandlePicker;
import net.ncguy.argent.editor.tools.picker.WorldEntityPicker;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.event.WorldEntitySelectedEvent;
import net.ncguy.argent.injector.ArgentInjector;
import net.ncguy.argent.injector.Inject;
import net.ncguy.argent.utils.InputManager;

/**
 * Created by Guy on 30/07/2016.
 */
public class ToolManager extends InputAdapter implements Disposable, RenderableProvider, WorldEntitySelectedEvent.WorldEntitySelectedListener {

    public static final int KEY_DEACTIVATE = Input.Keys.ESCAPE;
    private final WorldEntityPicker wePicker;
    private final ToolHandlePicker toolPicker;

    private Tool activeTool;

    public TranslateTool translateTool;
    public RotateTool rotateTool;
    public ScaleTool scaleTool;

    @Inject
    private InputManager inputManager;
    @Inject
    private ProjectManager projectManager;
    @Inject
    private CommandHistory commandHistory;

    public ToolManager(WorldEntityPicker wePicker, ToolHandlePicker toolPicker) {
        this.wePicker = wePicker;
        this.toolPicker = toolPicker;
        ArgentInjector.inject(this);
        this.activeTool = null;

        translateTool = new TranslateTool(projectManager, commandHistory, wePicker, toolPicker);
        rotateTool = new RotateTool(projectManager, commandHistory, wePicker, toolPicker);
        scaleTool = new ScaleTool(projectManager, commandHistory, wePicker, toolPicker);
    }

    public void activateTool(Tool tool) {
        deactivateTool();
        activeTool = tool;
        inputManager.addProcessor(3, activeTool);
    }

    public void deactivateTool() {
        if(activeTool == null) return;
        activeTool.reset();
        inputManager.removeProcessor(activeTool);
        activeTool = null;
    }

    public void setDefaultTool() {
        deactivateTool();
        activateTool(translateTool);
    }

    public Tool getActiveTool() {
        return activeTool;
    }

    public void act() {
        if(activeTool != null) activeTool.act();
    }

    @Override
    public boolean keyUp(int keycode) {
        if(keycode == KEY_DEACTIVATE) {
            if(activeTool != null) activeTool.reset();
            setDefaultTool();
            return true;
        }
        return false;
    }

    @Override
    public void dispose() {
        translateTool.dispose();
    }

    @Override
    public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
        if(activeTool != null) activeTool.getRenderables(renderables, pool);
    }

    public void selected(WorldEntity e) {
        if(activeTool != null) activeTool.selected(e);
    }

    @Override
    public void onWorldEntitySelected(WorldEntitySelectedEvent event) {
        selected(event.getEntity());
    }
}
