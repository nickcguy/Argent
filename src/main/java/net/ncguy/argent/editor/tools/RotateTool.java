package net.ncguy.argent.editor.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import net.ncguy.argent.Argent;
import net.ncguy.argent.editor.CommandHistory;
import net.ncguy.argent.editor.project.ProjectContext;
import net.ncguy.argent.editor.project.ProjectManager;
import net.ncguy.argent.editor.tools.picker.ToolHandlePicker;
import net.ncguy.argent.editor.tools.picker.WorldEntityPicker;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.utils.MathsUtils;
import net.ncguy.argent.utils.Meshes;

import static net.ncguy.argent.editor.tools.TransformTool.TransformState.*;

/**
 * Created by Guy on 31/07/2016.
 */
public class RotateTool extends TransformTool {

    private RotateHandle xHandle;
    private RotateHandle yHandle;
    private RotateHandle zHandle;
    private RotateHandle[] handles;

    private Vector3 tmp0 = new Vector3();
    private Vector3 tmp1 = new Vector3();
    private Quaternion tmpQuat = new Quaternion();

    private float lastRot = 0;

    public RotateTool(ProjectManager projectManager, CommandHistory history, WorldEntityPicker picker, ToolHandlePicker handlePicker) {
        super(projectManager, history, picker, handlePicker);
        this.name = "Rotate Tool";

        xHandle = RotateHandle.rotateHandle(X_HANDLE_ID, COLOUR_X);
        yHandle = RotateHandle.rotateHandle(Y_HANDLE_ID, COLOUR_Y);
        zHandle = RotateHandle.rotateHandle(Z_HANDLE_ID, COLOUR_Z);
        handles = new RotateHandle[]{xHandle, yHandle, zHandle};
    }

    @Override
    public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
        for (RotateHandle handle : handles)
            handle.getRenderables(renderables, pool);
    }

    @Override
    public void act() {
        super.act();
        ProjectContext context = projectManager.current();
        WorldEntity we = context.currScene.selected();
        if(we != null) {
            translateHandles();
            if(state == IDLE) return;
            float angle = getCurrentAngle();
            float rot = angle - lastRot;

            rot = -rot;

            boolean modified = false;
            if(state == TRANSFORM_X) {
                tmpQuat.setEulerAngles(0, rot, 0);
                we.rotate(tmpQuat);
                modified = true;
            }else if(state == TRANSFORM_Y) {
                tmpQuat.setEulerAngles(rot, 0, 0);
                we.rotate(tmpQuat);
                modified = true;
            }else if(state == TRANSFORM_Z) {
                tmpQuat.setEulerAngles(0, 0, rot);
                we.rotate(tmpQuat);
                modified = true;
            }

            if(modified) {
                modEvent.setEntity(we);
                Argent.event.post(modEvent);
            }
            lastRot = angle;
        }
    }

    private float getCurrentAngle() {
        ProjectContext context = projectManager.current();
        WorldEntity we = context.currScene.selected();
        if(we != null) {
            we.getPosition(tmp0);
            Vector3 pivot = context.currScene.sceneGraph.renderer.camera().project(tmp0);
            Vector3 mouse = tmp1.set(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(), 0);
            return MathsUtils.angle(pivot.x, pivot.y, mouse.x, mouse.y);
        }
        return 0;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        super.touchDown(screenX, screenY, pointer, button);
        if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            state = IDLE;
            return false;
        }
        ProjectContext context = projectManager.current();
        WorldEntity we = context.currScene.selected();
        if(we == null) return false;
        if(button == Input.Buttons.LEFT) {
            lastRot = getCurrentAngle();
            handlePicker.setHandles(handles);
            RotateHandle handle = (RotateHandle) handlePicker.pick(context.currScene, screenX, screenY);
            if(handle == null) {
                state = IDLE;
                return false;
            }

            if(handle.id == X_HANDLE_ID) state = TRANSFORM_X;
            else if(handle.id == Y_HANDLE_ID) state = TRANSFORM_Y;
            else if(handle.id == Z_HANDLE_ID) state = TRANSFORM_Z;

            handle.setColour(COLOUR_SELECTED);
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        super.touchUp(screenX, screenY, pointer, button);
        if(state != TransformState.IDLE) {
            xHandle.setColour(COLOUR_X);
            yHandle.setColour(COLOUR_Y);
            zHandle.setColour(COLOUR_Z);

            state = TransformState.IDLE;
        }
        return false;
    }

    @Override
    public void dispose() {
        super.dispose();
        xHandle.dispose();
        yHandle.dispose();
        zHandle.dispose();
    }


    @Override
    protected void translateHandles() {
        WorldEntity entity = projectManager.current().currScene.selected();
        if(entity == null) return;
        final Vector3 pos = entity.transform().getTranslation(tmp0);
        xHandle.position.set(pos);
        xHandle.applyTransform();
        yHandle.position.set(pos);
        yHandle.applyTransform();
        zHandle.position.set(pos);
        zHandle.applyTransform();
    }

    @Override
    protected void rotateHandles() {
        WorldEntity entity = projectManager.current().currScene.selected();
        if(entity == null) return;
        entity.transform().getRotation(tmpQuat);
        final Vector3 rot = new Vector3();
        rot.x = 90;
        rot.y = 90;
        rot.z = 90;

        xHandle.rotationEuler.set(0, rot.x, 0);
        xHandle.applyTransform();
        yHandle.rotationEuler.set(rot.y, 0, 0);
        yHandle.applyTransform();
        zHandle.rotationEuler.set(0, 0, rot.z);
        zHandle.applyTransform();
    }

    @Override
    protected void scaleHandles() {
        ProjectContext context = projectManager.current();
        WorldEntity entity = context.currScene.selected();
        if(entity == null) return;
        final Vector3 pos = entity.getPosition(tmp0);
        float scaleFactor = context.currScene.sceneGraph.renderer.camera().position.dst(pos) * .005f;
        if(scaleFactor < 1) scaleFactor = .1f;
        xHandle.scale.set(scaleFactor, scaleFactor, scaleFactor);
        xHandle.applyTransform();
        yHandle.scale.set(scaleFactor, scaleFactor, scaleFactor);
        yHandle.applyTransform();
        zHandle.scale.set(scaleFactor, scaleFactor, scaleFactor);
        zHandle.applyTransform();
    }

    @Override
    public void render(ModelBatch rootBatch) {
        for (RotateHandle handle : handles)
            handle.render(rootBatch);
    }

    public static class RotateHandle extends ToolHandle {

        public static RotateHandle rotateHandle(int id, Color colour) {
            Model model = Meshes.torus(new Material(ColorAttribute.createDiffuse(colour)), 20, 1, 50, 50);
            return new RotateHandle(id, model);
        }

        public RotateHandle(int id, Model model) {
            super(id, model);
        }

        @Override
        public void init() {

        }

        @Override
        public void act() {

        }

        @Override
        public void applyTransform() {
            rotation.setEulerAngles(rotationEuler.x, rotationEuler.y, rotationEuler.z);
            instance.transform.set(position, rotation, scale);
        }
    }

}
