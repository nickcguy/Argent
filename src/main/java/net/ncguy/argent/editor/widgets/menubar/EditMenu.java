package net.ncguy.argent.editor.widgets.menubar;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuItem;
import net.ncguy.argent.editor.CommandHistory;
import net.ncguy.argent.editor.EditorUI;
import net.ncguy.argent.injector.ArgentInjector;
import net.ncguy.argent.injector.Inject;

import static com.badlogic.gdx.Input.Keys.*;

/**
 * Created by Guy on 29/07/2016.
 */
public class EditMenu extends Menu {

    private MenuItem copy;
    private MenuItem paste;
    private MenuItem undo;
    private MenuItem redo;

    private MenuItem history;

    private EditorUI editorUI;

    @Inject
    protected CommandHistory commandHistory;

    public EditMenu(EditorUI editorUI) {
        super("Edit");
        this.editorUI = editorUI;
        ArgentInjector.inject(this);

        copy = new MenuItem("Copy");
        copy.setShortcut(CONTROL_LEFT, C);
        paste = new MenuItem("Paste");
        paste.setShortcut(CONTROL_LEFT, V);
        undo = new MenuItem("Undo");
        undo.setShortcut(CONTROL_LEFT, Z);
        redo = new MenuItem("Redo");
        redo.setShortcut(CONTROL_LEFT, Y);

        history = new MenuItem("History");

        copy.setDisabled(true);
        paste.setDisabled(true);

        addItem(copy);
        addItem(paste);
        addItem(undo);
        addItem(redo);
        addSeparator();
        addItem(history);

        setupListeners();
    }

    private void setupListeners() {
        undo.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                commandHistory.goBack();
            }
        });
        redo.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                commandHistory.goForward();
            }
        });
        history.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                displayHistoryDialog();
            }
        });
    }

    private void displayHistoryDialog() {
        Dialog dialog = new Dialog("Command History", VisUI.getSkin());
        Table dialogContent = new Table(VisUI.getSkin());
        ScrollPane scroller = new ScrollPane(dialogContent);
        dialog.add(scroller).expand().fill().row();
        int pointer = commandHistory.getPointer();
        final int[] index = {0};
        commandHistory.commands().forEach(cmd -> {
            Label lbl = new Label(cmd.getClass().getSimpleName(), VisUI.getSkin());
            if(pointer == index[0])
                lbl.setColor(Color.GREEN);
            dialogContent.add(lbl).expandX().fillX().row();
            index[0]++;
        });
        TextButton btn = new TextButton("Close", VisUI.getSkin());
        btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dialog.hide();
            }
        });
        dialogContent.add(btn).expandX().fillX();
        dialog.show(editorUI);
    }

}
