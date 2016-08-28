package net.ncguy.argent.project.widget.dialog;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisDialog;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import net.ncguy.argent.editor.project.ProjectManager;
import net.ncguy.argent.injector.ArgentInjector;
import net.ncguy.argent.injector.Inject;
import net.ncguy.argent.ui.FileChooserField;

/**
 * Created by Guy on 26/08/2016.
 */
public class NewProjectDialog extends VisDialog {

    private TextField projectName;
    private FileChooserField path;
    private TextButton createBtn;

    @Inject
    ProjectManager manager;

    public NewProjectDialog() {
        super("New Project");
        ArgentInjector.inject(this);
        addCloseButton();

        Table root = new Table(VisUI.getSkin());
        root.padTop(6).padRight(6).padBottom(22);
        add(root);

        path = new FileChooserField(FileChooser.Mode.SAVE);
        path.setMode(FileChooser.SelectionMode.DIRECTORIES);

        root.add("Project Name: ").right().padRight(5);
        root.add(projectName = new TextField("", VisUI.getSkin())).height(21).width(300).fillX();
        root.row().padTop(10);
        root.add("Location: ").right().padRight(5);
        root.add(path).row();

        createBtn = new TextButton("Create Project", VisUI.getSkin());
        root.add(createBtn).width(93).height(25).colspan(2);
        setupListeners();

        pack();
    }

    private void setupListeners() {
        createBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String name = projectName.getText();
                String path = NewProjectDialog.this.path.getPath();
                if(validateInput(name, path)) {
                    if(!path.endsWith("/"))
                        path += "/";
                    manager.registerContext(name, path);
                    close();
                }
            }
        });
    }

    private boolean validateInput(String name, String path) {
        return name != null && name.length() > 0 && path != null && path.length() > 0;
    }

}
