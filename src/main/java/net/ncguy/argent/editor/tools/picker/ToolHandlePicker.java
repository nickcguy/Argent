package net.ncguy.argent.editor.tools.picker;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import net.ncguy.argent.editor.project.EditorScene;
import net.ncguy.argent.editor.tools.ToolHandle;
import net.ncguy.argent.entity.attributes.PickerIDAttribute;
import net.ncguy.argent.misc.shader.Shaders;
import net.ncguy.argent.utils.ScreenshotUtils;

/**
 * Created by Guy on 30/07/2016.
 */
public class ToolHandlePicker extends BasePicker<ToolHandle> {

    ToolHandle[] handles;

    public ToolHandlePicker() {
        super();
    }

    public ToolHandlePicker setHandles(ToolHandle[] handles) {
        this.handles = handles;
        return this;
    }

    @Override
    public ToolHandle pick(EditorScene scene, int screenX, int screenY) {
        begin(scene.viewport);
        renderPickableScene(handles, scene.sceneGraph.renderer.batch(), scene.sceneGraph.renderer.camera());
        if(Gdx.input.isKeyPressed(Input.Keys.NUMPAD_7))
            ScreenshotUtils.saveScreenshot(fbo.getWidth(), fbo.getHeight(), "Picker");
        end();

        Pixmap map = getFBOPixmap(scene.viewport);
        int x = screenX;
        int y = screenY;
        System.out.printf("[X: %s, Y: %s]\n", x, y);

        int id = PickerIDAttribute.decode(map.getPixel(x, y));
        System.out.println(id);
        for (ToolHandle handle : handles) {
            if(handle.id == id) return handle;
        }
        return null;
    }

    private void renderPickableScene(ToolHandle[] handles, ModelBatch batch, PerspectiveCamera cam) {
        batch.begin(cam);
        for(ToolHandle handle : handles)
            handle.render(batch, Shaders.instance().pickerShader);
        batch.end();
    }

}
