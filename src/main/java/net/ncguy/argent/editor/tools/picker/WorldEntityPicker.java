package net.ncguy.argent.editor.tools.picker;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import net.ncguy.argent.editor.project.EditorScene;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.entity.attributes.PickerIDAttribute;

/**
 * Created by Guy on 30/07/2016.
 */
public class WorldEntityPicker extends BasePicker<WorldEntity> {

    public WorldEntityPicker() {
        super();
    }

    @Override
    public WorldEntity pick(EditorScene scene, int screenX, int screenY) {
        Pixmap map = getFBOPixmap();

        int x = screenX;
        int y = Gdx.graphics.getHeight() - screenY;

        int id = PickerIDAttribute.decode(map.getPixel(x, y));
        System.out.println(id);
        return getEntity(scene, id);
    }

    public WorldEntity getEntity(EditorScene scene, int id) {
        for (WorldEntity entity : scene.sceneGraph.getWorldEntities()) {
            int eId = entity.id();
            if(id == eId) return entity;
        }
        return null;
    }

}
