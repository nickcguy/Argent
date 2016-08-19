package net.ncguy.argent.vpl;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import net.ncguy.argent.editor.EditorUI;

/**
 * Created by Guy on 18/08/2016.
 */
public class VPLWidget extends Widget {

    private ScreenViewport viewport;
    private static Vector2 vec = new Vector2();
    private EditorUI ui;
    private VPLContainer pane;

    public VPLWidget(EditorUI ui, VPLContainer pane) {
        this.ui = ui;
        this.pane = pane;

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }
}
