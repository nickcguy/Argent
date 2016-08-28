package net.ncguy.argent.project.widget.details;

import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.VisUI;

/**
 * Created by Guy on 27/08/2016.
 */
public class ProjectDetailsStatusBar extends Table {

    public ProgressBar bar;

    public ProjectDetailsStatusBar() {
        super(VisUI.getSkin());
        bar = new ProgressBar(0, 100, 1, false, VisUI.getSkin());
        setBackground("menu-bg");
        add(bar).growX().left();
        bar.setAnimateDuration(.1f);
    }
}
