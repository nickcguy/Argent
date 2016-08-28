package net.ncguy.argent.ui;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.SingleFileChooserListener;
import net.ncguy.argent.editor.EditorUI;
import net.ncguy.argent.injector.ArgentInjector;
import net.ncguy.argent.injector.Inject;

/**
 * Created by Guy on 26/08/2016.
 */
public class FileChooserField extends Table {

    private FileSelected onSelected;
    private TextField field;
    private TextButton btn;
    private FileChooser chooser;

    private String path;
    private FileHandle handle;

    @Inject
    private EditorUI editorUI;

    public FileChooserField(FileChooser.Mode mode) {
        super(VisUI.getSkin());
        ArgentInjector.inject(this);
        field = new TextField("", VisUI.getSkin());
        btn = new TextButton("...", VisUI.getSkin());
        chooser = new FileChooser(mode);

        setupUI();
        setupListeners();
    }

    private void setupUI() {
        pad(5);
        add(field).expandX().fillX().padRight(5);
        add(btn).minWidth(48).row();
    }

    private void setupListeners() {

        chooser.setListener(new SingleFileChooserListener() {
            @Override
            protected void selected(FileHandle fileHandle) {
                handle = fileHandle;
                path = fileHandle.path();
                field.setText(path);
                if(onSelected != null)
                    onSelected.selected(fileHandle);
            }
        });

        btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Stage stage = FileChooserField.this.getStage();
                if(stage != null)
                    stage.addActor(chooser.fadeIn());
            }
        });
    }

    public void setMode(FileChooser.SelectionMode mode) {
        chooser.setSelectionMode(mode);
    }

    public void setCallback(FileSelected selected) { this.onSelected = selected; }
    public void setEdiable(boolean editable) { field.setDisabled(!editable); }
    public String getPath() { return path; }
    public FileHandle getHandle() { return handle; }

    public void clear() {
        field.setText("");
        handle = null;
        path = "";
    }

    public void setText(String text) { field.setText(text); }

    public static interface FileSelected {
        void selected(FileHandle handle);
    }

}
