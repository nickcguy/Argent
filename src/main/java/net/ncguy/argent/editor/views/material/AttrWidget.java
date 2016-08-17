package net.ncguy.argent.editor.views.material;

import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import net.ncguy.argent.Argent;
import net.ncguy.argent.editor.project.ProjectManager;
import net.ncguy.argent.event.MaterialComponentChangeEvent;
import net.ncguy.argent.event.WorldEntityModifiedEvent;
import net.ncguy.argent.injector.Inject;

/**
 * Created by Guy on 04/08/2016.
 */
public abstract class AttrWidget<T extends Attribute> extends Tab {

    protected Material mtl;
    public T attribute;

    protected Table content;

    @Inject
    protected ProjectManager projectManager;

    public AttrWidget(Material mtl, T attribute) {
        super(false, true);
        this.mtl = mtl;
        this.attribute = attribute;

        content = new Table(VisUI.getSkin());

        setupUI();
        setupListeners();

        setValues();
    }

    public void delete() {
        if(!isDeletable()) return;
        onDelete();
        Argent.event.post(new MaterialComponentChangeEvent(MaterialComponentChangeEvent.ChangeType.REMOVE));
        Argent.event.post(new WorldEntityModifiedEvent(projectManager.current().currScene.selected()));
    }

    public boolean isDeletable() {
        return isCloseableByUser();
    }

    protected void onDelete() {
        mtl.remove(attribute.type);
    }
    protected abstract void setupUI();
    protected abstract void setupListeners();
    protected abstract void setValues();

    @Override
    public String getTabTitle() {
        return Attribute.getAttributeAlias(attribute.type);
    }

    @Override
    public Table getContentTable() {
        return content;
    }
}
