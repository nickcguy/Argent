package net.ncguy.argent.assets;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.kotcrab.vis.ui.widget.PopupMenu;
import net.ncguy.argent.editor.project.ProjectManager;
import net.ncguy.argent.injector.ArgentInjector;
import net.ncguy.argent.injector.Inject;

/**
 * Created by Guy on 01/08/2016.
 */
public abstract class ArgAsset<T> {

    protected T asset;
    protected String fileName;

    public String getFileName() {
        return fileName;
    }

    @Inject
    protected transient ProjectManager projectManager;

    public ArgAsset() {
        ArgentInjector.inject(this);
    }

    public abstract String tag();
    public abstract String name();
    public abstract void name(String name);

    public Drawable icon() {
        return null;
    }

    public T getAsset() { return asset; }

    public void setAsset(T asset) { this.asset = asset; }

    public PopupMenu contextMenu() {
        return null;
    }

    public ProjectManager getProjectManager() {
        if(projectManager == null)
            ArgentInjector.inject(this);
        return projectManager;
    }
}
