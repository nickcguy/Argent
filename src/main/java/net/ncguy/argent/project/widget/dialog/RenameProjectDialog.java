package net.ncguy.argent.project.widget.dialog;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisDialog;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;
import net.ncguy.argent.event.project.UpdateProjectEvent;
import net.ncguy.argent.project.ProjectMeta;

/**
 * Created by Guy on 27/08/2016.
 */
public class RenameProjectDialog extends VisDialog {

    private ProjectMeta meta;
    private VisValidatableTextField field;
    private TextButton cancelBtn;
    private TextButton confirmBtn;

    public RenameProjectDialog(ProjectMeta meta) {
        super("Rename project: "+meta.name);
        this.meta = meta;
        initUI();
        attachListeners();
    }

    private void initUI() {
        field = new VisValidatableTextField(meta.name);
        field.addValidator(s -> !s.isEmpty());
        cancelBtn = new TextButton("Cancel", VisUI.getSkin());
        confirmBtn = new TextButton("Confirm", VisUI.getSkin());

        Table root = new Table(VisUI.getSkin());
        root.padTop(6).padRight(6).padBottom(22);
        add(root);

        root.add("Name").left().padRight(4).padBottom(4);
        root.add(field).expandX().fillX().padBottom(4).row();

        root.add(cancelBtn).height(25).expandX().fillX().bottom().padRight(4);
        root.add(confirmBtn).height(25).expandX().fillX().bottom().row();

        setDebug(false, true);
    }

    private void attachListeners() {
        cancelBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });
        confirmBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(!field.isInputValid()) return;
                meta.name = field.getText();
                new UpdateProjectEvent(meta).fire();
                hide();
            }
        });
    }

}
