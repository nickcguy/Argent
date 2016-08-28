package net.ncguy.argent.project.widget.dialog;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisDialog;
import net.ncguy.argent.editor.project.ProjectManager;
import net.ncguy.argent.injector.ArgentInjector;
import net.ncguy.argent.injector.Inject;
import net.ncguy.argent.project.ProjectMeta;

/**
 * Created by Guy on 27/08/2016.
 */
public class DeleteProjectDialog extends VisDialog {

    private ProjectMeta meta;
    private TextButton cancelBtn;
    private TextButton deleteBtn;

    @Inject
    ProjectManager manager;

    public DeleteProjectDialog(ProjectMeta meta) {
        super("Delete project: " + meta.name);
        ArgentInjector.inject(this);
        this.meta = meta;
        initUI();
        attachListeners();
    }

    private void initUI() {
        Table root = new Table(VisUI.getSkin());
        root.padTop(6).padRight(6).padBottom(22);
        add(root);

        cancelBtn = new TextButton("Cancel", VisUI.getSkin());
        root.add(cancelBtn).width(93).height(25).padRight(4);

        deleteBtn = new TextButton("Delete Project", VisUI.getSkin());
        root.add(deleteBtn).width(93).height(25).row();
    }

    private void attachListeners() {
        cancelBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });
        deleteBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                manager.removeContext(meta);
                hide();
            }
        });
    }

}
