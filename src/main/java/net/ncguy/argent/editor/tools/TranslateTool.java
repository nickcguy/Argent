package net.ncguy.argent.editor.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import net.ncguy.argent.Argent;
import net.ncguy.argent.editor.CommandHistory;
import net.ncguy.argent.editor.project.ProjectManager;
import net.ncguy.argent.editor.tools.picker.ToolHandlePicker;
import net.ncguy.argent.editor.tools.picker.WorldEntityPicker;
import net.ncguy.argent.editor.widgets.component.commands.TranslateCommand;
import net.ncguy.argent.entity.WorldEntity;

import static com.badlogic.gdx.graphics.GL30.GL_TRIANGLES;

/**
 * Created by Guy on 30/07/2016.
 */
public class TranslateTool extends TransformTool {

    private final float ARROW_THICKNESS = .4f;
    private final float ARROW_CAP_SIZE = .15f;
    private final int ARROW_DIVISIONS = 12;

    private boolean initTranslate = true;

    private TranslateHandle xHandle;
    private TranslateHandle yHandle;
    private TranslateHandle zHandle;
    private TranslateHandle xzHandle;
    private TranslateHandle[] handles;

    private Vector3 lastPos = new Vector3();
    private boolean worldSpace = true;
    private Vector3 tmp0 = new Vector3();
    private TranslateCommand cmd;

    public TranslateTool(ProjectManager projectManager, CommandHistory history, WorldEntityPicker picker, ToolHandlePicker handlePicker) {
        super(projectManager, history, picker, handlePicker);
        ModelBuilder mb = new ModelBuilder();

        int attrs = VertexAttributes.Usage.Position;

        Model xHandleModel = mb.createArrow(0, 0, 0, 2, 0, 0, ARROW_CAP_SIZE, ARROW_THICKNESS, ARROW_DIVISIONS, GL_TRIANGLES, new Material(ColorAttribute.createDiffuse(COLOUR_X)), attrs);
        Model yHandleModel = mb.createArrow(0, 0, 0, 0, 2, 0, ARROW_CAP_SIZE, ARROW_THICKNESS, ARROW_DIVISIONS, GL_TRIANGLES, new Material(ColorAttribute.createDiffuse(COLOUR_Y)), attrs);
        Model zHandleModel = mb.createArrow(0, 0, 0, 0, 0, 2, ARROW_CAP_SIZE, ARROW_THICKNESS, ARROW_DIVISIONS, GL_TRIANGLES, new Material(ColorAttribute.createDiffuse(COLOUR_Z)), attrs);
        Model xzHandleModel = mb.createSphere(1, 1, 1, 20, 20, new Material(ColorAttribute.createDiffuse(COLOUR_XZ)), attrs);

        xHandle = new TranslateHandle(X_HANDLE_ID, xHandleModel);
        yHandle = new TranslateHandle(Y_HANDLE_ID, yHandleModel);
        zHandle = new TranslateHandle(Z_HANDLE_ID, zHandleModel);
        xzHandle = new TranslateHandle(XZ_HANDLE_ID, xzHandleModel);
        handles = new TranslateHandle[]{xHandle, yHandle, zHandle, xzHandle};
    }

    @Override
    public void act() {
        super.act();

        if(projectManager.current().currScene.selected() != null) {
            translateHandles();
            if(state == TransformState.IDLE) return;

            WorldEntity we = projectManager.current().currScene.selected();

            Ray ray = projectManager.current().currScene.sceneGraph.renderer.camera().getPickRay(Gdx.input.getX(), Gdx.input.getY());
            Vector3 rayEnd = we.getLocalPosition(tmp0);
            float dst = projectManager.current().currScene.sceneGraph.renderer.camera().position.dst(rayEnd);
            rayEnd = ray.getEndPoint(rayEnd, dst);

            if(initTranslate) {
                initTranslate = false;
                lastPos.set(rayEnd);
            }

            boolean modified = false;
            Vector3 vec = new Vector3();
            if(state == TransformState.TRANSFORM_XZ) {
                vec.set(rayEnd.x - lastPos.x, 0, rayEnd.z - lastPos.z);
                modified = true;
            } else if(state == TransformState.TRANSFORM_X) {
                vec.set(rayEnd.x - lastPos.x,
                        0, 0);
                modified = true;
            } else if(state == TransformState.TRANSFORM_Y) {
                vec.set(0,
                        rayEnd.y - lastPos.y, 0);
                modified = true;
            } else if(state == TransformState.TRANSFORM_Z) {
                vec.set(0, 0,
                        rayEnd.z - lastPos.z);
                modified = true;
            }

            // TODO translation in global sapce
//            if(globalSpace) {
//                System.out.println("Before: " + vec);
//                System.out.println("After: " + vec);
//            }

            we.translate(vec);

            if(modified) {
                modEvent.setEntity(we);
                Argent.event.post(modEvent);
            }

            lastPos.set(rayEnd);
        }
    }

    @Override
    public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
        for (TranslateHandle handle : handles)
            handle.getRenderables(renderables, pool);
    }

    @Override
    protected void translateHandles() {
        final Vector3 pos = projectManager.current().currScene.selected().transform().getTranslation(tmp0);
        xHandle.position.set(pos);
        xHandle.applyTransform();
        yHandle.position.set(pos);
        yHandle.applyTransform();
        zHandle.position.set(pos);
        zHandle.applyTransform();
        xzHandle.position.set(pos);
        xzHandle.applyTransform();
    }

    @Override
    protected void rotateHandles() {

    }

    @Override
    protected void scaleHandles() {
        Vector3 pos = projectManager.current().currScene.selected().getPosition(tmp0);
        float scaleFactor = projectManager.current().currScene.sceneGraph.renderer.camera().position.dst(pos) * 0.25f;
        scaleFactor = 1;
        xHandle.scale.set(scaleFactor * 0.7f, scaleFactor / 2, scaleFactor / 2);
        xHandle.applyTransform();

        yHandle.scale.set(scaleFactor / 2, scaleFactor * 0.7f, scaleFactor / 2);
        yHandle.applyTransform();

        zHandle.scale.set(scaleFactor / 2, scaleFactor / 2, scaleFactor * 0.7f);
        zHandle.applyTransform();

        xzHandle.scale.set(scaleFactor*0.13f,scaleFactor*0.13f, scaleFactor*0.13f);
        xzHandle.applyTransform();
    }

    @Override
    public void render(ModelBatch rootBatch) {
        for (TranslateHandle handle : handles)
            handle.render(rootBatch);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        super.touchDown(screenX, screenY, pointer, button);

//        screenY = Gdx.graphics.getHeight() - screenY;

        WorldEntity sel = projectManager.current().currScene.selected();

        if(button == Input.Buttons.LEFT && sel != null) {
            handlePicker.setHandles(handles);
            TranslateHandle handle = (TranslateHandle) handlePicker.pick(projectManager.current().currScene, screenX, screenY);
            if(handle == null) {
                state = TransformState.IDLE;
                return false;
            }

            if(handle.id == XZ_HANDLE_ID) {
                state = TransformState.TRANSFORM_XZ;
                initTranslate = true;
                xzHandle.setColour(COLOUR_SELECTED);
            } else if(handle.id == X_HANDLE_ID) {
                state = TransformState.TRANSFORM_X;
                initTranslate = true;
                xHandle.setColour(COLOUR_SELECTED);
            } else if(handle.id == Y_HANDLE_ID) {
                state = TransformState.TRANSFORM_Y;
                initTranslate = true;
                yHandle.setColour(COLOUR_SELECTED);
            } else if(handle.id == Z_HANDLE_ID) {
                state = TransformState.TRANSFORM_Z;
                initTranslate = true;
                zHandle.setColour(COLOUR_SELECTED);
            }

            if(state != TransformState.IDLE) {
                cmd = new TranslateCommand(sel);
                cmd.setBefore(sel.getLocalPosition(tmp0));
            }
            return true;
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
            xzHandle.setColour(COLOUR_XZ);

            cmd.setAfter(projectManager.current().currScene.selected().getLocalPosition(tmp0));
            history.add(cmd);
            cmd = null;
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
        xzHandle.dispose();
    }

    private class TranslateHandle extends ToolHandle {

        public TranslateHandle(int id, Model model) {
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
            rotation.setEulerAngles(rotationEuler.y, rotationEuler.x, rotationEuler.z);
            instance.transform.set(position, rotation, scale);
        }


    }
}
