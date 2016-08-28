package net.ncguy.argent.editor.widgets.menubar;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuItem;
import net.ncguy.argent.GlobalSettings;
import net.ncguy.argent.editor.EditorUI;

/**
 * Created by Guy on 17/08/2016.
 */
public class SettingsMenu extends Menu {

    private EditorUI editorUI;

    private MenuItem displayLights;
    private MenuItem displayShadows;

    public SettingsMenu(EditorUI editorUI) {
        super("Settings");
        this.editorUI = editorUI;
        setupUI();
    }

    private void setupUI() {
        displayLights = new MenuItem("Display Lights", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GlobalSettings.toggleBoolVar(GlobalSettings.VarKeys.bool_LIGHTDEBUG);
            }
        });
        displayShadows = new MenuItem("Display Shadows [NYI]", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GlobalSettings.toggleBoolVar(GlobalSettings.VarKeys.bool_SHADOWS);
            }
        });

        addItem(displayLights);
        addItem(displayShadows);
    }

}
