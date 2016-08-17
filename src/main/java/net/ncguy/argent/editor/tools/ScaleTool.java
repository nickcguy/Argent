package net.ncguy.argent.editor.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import net.ncguy.argent.Argent;
import net.ncguy.argent.editor.CommandHistory;
import net.ncguy.argent.editor.project.ProjectManager;
import net.ncguy.argent.editor.tools.picker.ToolHandlePicker;
import net.ncguy.argent.editor.tools.picker.WorldEntityPicker;
import net.ncguy.argent.editor.widgets.component.commands.ScaleCommand;
import net.ncguy.argent.entity.WorldEntity;

/**
 * Created by Guy on 31/07/2016.
 */
public class ScaleTool extends TransformTool {

    private ScaleHandle xHandle;
    private ScaleHandle yHandle;
    private ScaleHandle zHandle;
    private ScaleHandle[] handles;

    private Vector3 tmp0 = new Vector3();
    private Vector3 lastPos = new Vector3();

    private boolean initScale = true;
    private ScaleCommand cmd;

    public ScaleTool(ProjectManager projectManager, CommandHistory history, WorldEntityPicker picker, ToolHandlePicker handlePicker) {
        super(projectManager, history, picker, handlePicker);
        this.name = "Scale Tool";

        xHandle = ScaleHandle.scaleHandle(X_HANDLE_ID, COLOUR_X, Vector3.X.cpy());
        yHandle = ScaleHandle.scaleHandle(Y_HANDLE_ID, COLOUR_Y, Vector3.Y.cpy());
        zHandle = ScaleHandle.scaleHandle(Z_HANDLE_ID, COLOUR_Z, Vector3.Z.cpy());
        handles = new ScaleHandle[]{xHandle, yHandle, zHandle};
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

            if(initScale) {
                initScale = false;
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

            we.scale(vec);

            if(modified) {
                modEvent.setEntity(we);
                Argent.event.post(modEvent);
            }

            lastPos.set(rayEnd);
        }
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        super.touchDown(screenX, screenY, pointer, button);

//        screenY = Gdx.graphics.getHeight() - screenY;

        WorldEntity sel = projectManager.current().currScene.selected();

        if(button == Input.Buttons.LEFT && sel != null) {
            handlePicker.setHandles(handles);
            ScaleHandle handle = (ScaleHandle) handlePicker.pick(projectManager.current().currScene, screenX, screenY);
            if(handle == null) {
                state = TransformState.IDLE;
                return false;
            }

            if(handle.id == X_HANDLE_ID) {
                state = TransformState.TRANSFORM_X;
                initScale = true;
                xHandle.setColour(COLOUR_SELECTED);
            } else if(handle.id == Y_HANDLE_ID) {
                state = TransformState.TRANSFORM_Y;
                initScale = true;
                yHandle.setColour(COLOUR_SELECTED);
            } else if(handle.id == Z_HANDLE_ID) {
                state = TransformState.TRANSFORM_Z;
                initScale = true;
                zHandle.setColour(COLOUR_SELECTED);
            }

            if(state != TransformState.IDLE) {
                cmd = new ScaleCommand(sel);
                cmd.setBefore(sel.getScale(tmp0));
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

            cmd.setAfter(projectManager.current().currScene.selected().getScale(tmp0));
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
    }

    @Override
    public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
        for (ScaleHandle handle : handles)
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
    }

    @Override
    protected void rotateHandles() {

    }

    @Override
    protected void scaleHandles() {
        Vector3 pos = projectManager.current().currScene.selected().getPosition(tmp0);
        float scaleFactor = projectManager.current().currScene.sceneGraph.renderer.camera().position.dst(pos) * 0.25f;
        xHandle.scale.set(scaleFactor * 0.7f, scaleFactor / 2, scaleFactor / 2);
        xHandle.applyTransform();

        yHandle.scale.set(scaleFactor / 2, scaleFactor * 0.7f, scaleFactor / 2);
        yHandle.applyTransform();

        zHandle.scale.set(scaleFactor / 2, scaleFactor / 2, scaleFactor * 0.7f);
        zHandle.applyTransform();
    }

    public static class ScaleHandle extends ToolHandle  {

        public static ScaleHandle scaleHandle(int id, Color colour, Vector3 direction) {
            direction.add(.1f);
            ModelBuilder mb = new ModelBuilder();
            mb.begin();
            Material mtl = new Material(ColorAttribute.createDiffuse(colour));
            BoxShapeBuilder.build(mb.part("line", GL30.GL_TRIANGLES, VertexAttributes.Usage.Position, mtl), direction.x, direction.y, direction.z);
            BoxShapeBuilder.build(mb.part("block", GL30.GL_TRIANGLES, VertexAttributes.Usage.Position, mtl), 1, 1, 1);

            Model model = mb.end();
            model.meshParts.get(1).center.set(direction.sub(.1f));
            return new ScaleHandle(id, model);
        }

        public ScaleHandle(int id, Model model) {
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

        }
    }

}
