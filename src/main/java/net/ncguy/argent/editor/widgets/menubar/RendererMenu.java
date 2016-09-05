package net.ncguy.argent.editor.widgets.menubar;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuItem;
import net.ncguy.argent.GlobalSettings;
import net.ncguy.argent.editor.EditorUI;
import net.ncguy.argent.render.argent.ArgentRenderer;

import static net.ncguy.argent.render.argent.ArgentRenderer.*;

/**
 * Created by Guy on 05/09/2016.
 */
public class RendererMenu extends Menu {

    private EditorUI editorUI;

    public RendererMenu(EditorUI editorUI) {
        super("Active Renderer");
        this.editorUI = editorUI;
        setupUI();
    }

    private void setupUI() {
        addItem(new MenuItem("Position", new RendererSelector(ltg_POSITION)));
        addItem(new MenuItem("Textures", new RendererSelector(ltg_TEXTURES)));
        addItem(new MenuItem("Lighting", new RendererSelector(ltg_LIGHTING)));
        addItem(new MenuItem("Ambient Geometry", new RendererSelector(ltg_GEOMETRY)));
    }

    public static class RendererSelector extends ChangeListener {

        private ArgentRenderer.FBOAttachment attachment;

        public RendererSelector(ArgentRenderer.FBOAttachment attachment) {
            this.attachment = attachment;
        }

        @Override
        public void changed(ChangeEvent event, Actor actor) {
            GlobalSettings.rendererIndex(this.attachment.id);
        }
    }

}
