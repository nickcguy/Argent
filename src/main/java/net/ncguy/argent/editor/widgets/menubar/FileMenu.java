package net.ncguy.argent.editor.widgets.menubar;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.SingleFileChooserListener;
import net.ncguy.argent.editor.EditorUI;
import net.ncguy.argent.editor.project.ProjectManager;
import net.ncguy.argent.injector.ArgentInjector;
import net.ncguy.argent.injector.Inject;

import java.util.function.Consumer;

import static com.badlogic.gdx.Input.Keys.*;
import static com.kotcrab.vis.ui.widget.file.FileChooser.Mode.OPEN;

/**
 * Created by Guy on 29/07/2016.
 */
public class FileMenu extends Menu {

    private MenuItem newProject;
    private MenuItem loadProject;
    private MenuItem saveProject;
    private MenuItem exit;

    private FileChooser fileChooser;

    @Inject
    private ProjectManager projectManager;

    private EditorUI editorUI;

    public FileMenu(EditorUI editorUI) {
        super("File");
        this.editorUI = editorUI;
        ArgentInjector.inject(this);

        fileChooser = new FileChooser(OPEN);
        fileChooser.setSelectionMode(FileChooser.SelectionMode.FILES);

        newProject = new MenuItem("New Project");
        newProject.setShortcut(CONTROL_LEFT, N);
        loadProject = new MenuItem("Load Project");
        loadProject.setShortcut(CONTROL_LEFT, O);
        saveProject = new MenuItem("Save Project");
        saveProject.setShortcut(CONTROL_LEFT, S);
        exit = new MenuItem("Exit");

        addItem(newProject);
        addItem(loadProject);
        addItem(saveProject);
        addSeparator();
        addItem(exit);

        setupListeners();
    }

    private void setupListeners() {
        newProject.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                openFileChooser(projectManager::newProject);
            }
        });

        loadProject.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                openFileChooser(projectManager::loadProject);
            }
        });

        saveProject.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                openFileChooser(projectManager::saveProject);
            }
        });
    }

    private void openFileChooser(Consumer<FileHandle> file) {
        fileChooser.setListener(new SingleFileChooserListener() {
            @Override
            protected void selected(FileHandle handle) {
                file.accept(handle);
            }
        });
        editorUI.addActor(fileChooser.fadeIn());
    }

}
