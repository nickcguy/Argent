package net.ncguy.argent.editor.widgets.menubar;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuItem;
import net.ncguy.argent.ArgentGame;
import net.ncguy.argent.injector.ArgentInjector;
import net.ncguy.argent.injector.Inject;
import net.ncguy.argent.project.ProjectSelectorScreen;

/**
 * Created by Guy on 29/07/2016.
 */
public class FileMenu extends Menu {

    @Inject
    private ArgentGame game;

    public FileMenu() {
        super("File");
        ArgentInjector.inject(this);

        addItem(new MenuItem("Return to project manager", new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                game.setScreen(new ProjectSelectorScreen());
            }
        }));
    }

}
