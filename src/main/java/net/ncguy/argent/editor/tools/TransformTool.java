package net.ncguy.argent.editor.tools;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import net.ncguy.argent.editor.CommandHistory;
import net.ncguy.argent.editor.project.ProjectManager;
import net.ncguy.argent.editor.tools.picker.ToolHandlePicker;
import net.ncguy.argent.editor.tools.picker.WorldEntityPicker;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.event.WorldEntityModifiedEvent;

import static net.ncguy.argent.editor.tools.TransformTool.TransformState.IDLE;

/**
 * Created by Guy on 30/07/2016.
 */
public abstract class TransformTool extends SelectionTool {

    protected enum TransformState {
        TRANSFORM_X,
        TRANSFORM_Y,
        TRANSFORM_Z,
        TRANSFORM_XY,
        TRANSFORM_XZ,
        TRANSFORM_YZ,
        TRANSFORM_XYZ,
        IDLE
    }

    protected static Color COLOUR_X = Color.RED;
    protected static Color COLOUR_Y = Color.GREEN;
    protected static Color COLOUR_Z = Color.BLUE;
    protected static Color COLOUR_XY = Color.YELLOW;
    protected static Color COLOUR_XZ = Color.PURPLE;
    protected static Color COLOUR_YZ = Color.CYAN;
    protected static Color COLOUR_XYZ = Color.WHITE;

    // TODO change selected colour
    protected static Color COLOUR_SELECTED = Color.YELLOW;

    protected static final int X_HANDLE_ID  = COLOUR_X.toIntBits();
    protected static final int Y_HANDLE_ID  = COLOUR_Y.toIntBits();
    protected static final int Z_HANDLE_ID  = COLOUR_Z.toIntBits();
    protected static final int XY_HANDLE_ID = COLOUR_XY.toIntBits();
    protected static final int XZ_HANDLE_ID = COLOUR_XZ.toIntBits();
    protected static final int YZ_HANDLE_ID = COLOUR_YZ.toIntBits();
    protected static final int XYZ_HANDLE_ID = COLOUR_XYZ.toIntBits();

    protected ToolHandlePicker handlePicker;
    protected WorldEntityModifiedEvent modEvent;

    protected TransformState state = IDLE;

    public TransformTool(ProjectManager projectManager, CommandHistory history, WorldEntityPicker picker, ToolHandlePicker handlePicker) {
        super(projectManager, history, picker);
        this.modEvent = new WorldEntityModifiedEvent();
        this.handlePicker = handlePicker;
    }

    protected abstract void translateHandles();
    protected abstract void rotateHandles();
    protected abstract void scaleHandles();

    @Override
    public void selected(WorldEntity selection) {
        super.selected(selection);
        scaleHandles();
        rotateHandles();
        translateHandles();
    }

    @Override
    public abstract void render(ModelBatch rootBatch);

}
