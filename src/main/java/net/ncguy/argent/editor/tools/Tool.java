package net.ncguy.argent.editor.tools;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Disposable;
import net.ncguy.argent.editor.CommandHistory;
import net.ncguy.argent.editor.project.ProjectManager;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.utils.StringUtils;

/**
 * Created by Guy on 30/07/2016.
 */
public abstract class Tool extends InputAdapter implements Disposable, RenderableProvider {

    protected ProjectManager projectManager;
    protected CommandHistory history;

    protected String name;
    protected Drawable icon;

    public Tool(ProjectManager projectManager, CommandHistory history) {
        this.projectManager = projectManager;
        this.history = history;
    }

    public String getName() {
        if(this.name.length() == 0)
            return this.name;
        return StringUtils.splitCamelCase(this.getClass().getSimpleName());
    }

    public Drawable getIcon() {
        return icon;
    }

    public void selected(WorldEntity selection) {
        projectManager.current().currScene.select(selection);
    }

    public abstract void reset();
    public abstract void render();
    public abstract void act();

}
