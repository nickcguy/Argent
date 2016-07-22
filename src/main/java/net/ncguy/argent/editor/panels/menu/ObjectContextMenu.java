package net.ncguy.argent.editor.panels.menu;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.PopupMenu;
import net.ncguy.argent.editor.EditorRoot;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.ui.ArgentList;
import net.ncguy.argent.utils.AppUtils;

/**
 * Created by Guy on 22/07/2016.
 */
public class ObjectContextMenu<T extends WorldEntity> {

    T target;
    PopupMenu rootMenu;
    MenuItem addObjectItem;
    MenuItem delObjectItem;
    EditorRoot<T> editor;

    public ObjectContextMenu(EditorRoot<T> editor) {
        this.editor = editor;
        init();
    }

    protected void init() {
        rootMenu = new PopupMenu();
        rootMenu.addItem(addObjectItem = new MenuItem("Add object", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                addObject();
            }
        }));
        rootMenu.addItem(delObjectItem = new MenuItem("Delete object", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                delObject();
            }
        }));

    }

    public void addObject() {
        T newInstance = this.editor.world().buildInstance();
        this.editor.world().addInstance(newInstance);
        this.editor.select(newInstance);
        this.editor.objDataPanel.populateObjList();
    }
    public void delObject() {
        this.editor.world().removeInstance(target);
        this.editor.objDataPanel.populateObjList();
    }

    public T target() { return target; }
    public void target(T selected) {
        this.target = selected;
        this.delObjectItem.setDisabled(this.target == null);
    }

    public void show(ArgentList.ArgentListElement<T> element) {
        target(element.object);
        show();
    }

    public void show() {
        show(AppUtils.Input.getPackedCursorPos());
    }
    public void show(Vector2 pos) {
        show(pos.x, pos.y);
    }
    public void show(float x, float y) {
        rootMenu.showMenu(editor.stage(), x, y);
        rootMenu.addAction(Actions.sequence(
                Actions.alpha(0),
                Actions.fadeIn(.3f)
        ));
    }

    public void hide() {
        rootMenu.addAction(Actions.sequence(
                Actions.alpha(1),
                Actions.fadeOut(.3f),
                Actions.run(rootMenu::remove)
        ));
    }
}
