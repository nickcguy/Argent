package net.ncguy.argent.editor.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import net.ncguy.argent.editor.CommandHistory;
import net.ncguy.argent.editor.project.ProjectManager;
import net.ncguy.argent.editor.tools.picker.WorldEntityPicker;
import net.ncguy.argent.entity.WorldEntity;

/**
 * Created by Guy on 30/07/2016.
 */
public class SelectionTool extends Tool {

    protected WorldEntityPicker wePicker;

    public SelectionTool(ProjectManager projectManager, CommandHistory history, WorldEntityPicker picker) {
        super(projectManager, history);
        this.icon = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("assets/icons/tool_selection.png"))));
        this.wePicker = picker;
    }

    @Override
    public void reset() {
        projectManager.current().currScene.select(null);
    }

    @Override
    public void render(ModelBatch rootBatch) {

    }

    @Override
    public void act() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if(button == Input.Buttons.RIGHT) {
            WorldEntity selection = wePicker.pick(projectManager.current().currScene, screenX, screenY);
            if(selection != null) {
                if(selection != projectManager.current().currScene.selected()) {
                    selected(selection);
                }
            }
        }
        return false;
    }

    @Override
    public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {

    }
}
