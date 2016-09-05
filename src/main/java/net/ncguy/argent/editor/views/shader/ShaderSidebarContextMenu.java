package net.ncguy.argent.editor.views.shader;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.PopupMenu;

/**
 * Created by Guy on 31/08/2016.
 */
public class ShaderSidebarContextMenu extends PopupMenu {

    public ShaderSidebarContextMenu() {
        super();
        initUI();
    }

    private void initUI() {
        addItem(new MenuItem("New Shader", new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                new NewShaderDialog().show(getStage());
            }
        }));
    }
}
