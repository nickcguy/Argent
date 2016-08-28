package net.ncguy.argent.project.widget.selector;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.PopupMenu;
import net.ncguy.argent.event.project.UpdateProjectEvent;
import net.ncguy.argent.project.ProjectMeta;
import net.ncguy.argent.project.widget.dialog.DeleteProjectDialog;
import net.ncguy.argent.project.widget.dialog.RenameProjectDialog;
import net.ncguy.argent.utils.AppUtils;

/**
 * Created by Guy on 27/08/2016.
 */
public class ProjectWidgetContextMenu extends PopupMenu {


    private ProjectMeta attachedMeta;

    public ProjectWidgetContextMenu(ProjectMeta attachedMeta) {
        super();
        this.attachedMeta = attachedMeta;
        initUI();
    }

    private void initUI() {
        addItem(new MenuItem("Rename", new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                new RenameProjectDialog(attachedMeta).show(getStage());
            }
        }));
        addItem(new MenuItem("Recalculate", new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                new UpdateProjectEvent(attachedMeta).fire();
            }
        }));
        addItem(new MenuItem("Open in Explorer", new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                AppUtils.General.openExplorer(attachedMeta.path);
            }
        }));
        addSeparator();
        addItem(new MenuItem("Delete", new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                new DeleteProjectDialog(attachedMeta).show(getStage());
            }
        }));
    }

}
