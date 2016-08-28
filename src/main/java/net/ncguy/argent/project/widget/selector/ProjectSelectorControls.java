package net.ncguy.argent.project.widget.selector;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.VisUI;
import net.ncguy.argent.project.widget.dialog.NewProjectDialog;

/**
 * Created by Guy on 26/08/2016.
 */
public class ProjectSelectorControls extends Table {

    private TextButton addProject;
    private TextButton importProject;

    private NewProjectDialog newDialog;

    public ProjectSelectorControls() {
        super(VisUI.getSkin());
        setBackground("default-pane");


        addProject = new TextButton("New Project", VisUI.getSkin());
        importProject = new TextButton("Import Project [NYI]", VisUI.getSkin());

        addProject.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                newProjectDialog();
            }
        });

        add(addProject).row();
        add(importProject).row();
    }

    public void newProjectDialog() {
        if(newDialog == null)
            newDialog = new NewProjectDialog();
        newDialog.pack();
        newDialog.show(getStage());
    }

}
