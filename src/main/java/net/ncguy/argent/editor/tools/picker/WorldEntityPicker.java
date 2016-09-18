package net.ncguy.argent.editor.tools.picker;

import net.ncguy.argent.editor.project.EditorScene;
import net.ncguy.argent.entity.WorldEntity;

/**
 * Created by Guy on 30/07/2016.
 */
public class WorldEntityPicker extends BasePicker<WorldEntity> {

    public WorldEntityPicker() {
        super();
    }

    @Override
    public WorldEntity pick(EditorScene scene, int screenX, int screenY) {
//        begin(scene.viewport);
        return null;
    }

    public WorldEntity getEntity(EditorScene scene, int id) {
        for (WorldEntity entity : scene.sceneGraph.getWorldEntities()) {
            int eId = entity.id();
            if(id == eId) return entity;
        }
        return null;
    }

}
