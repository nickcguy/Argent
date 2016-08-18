package net.ncguy.argent.vpl;

import com.badlogic.gdx.Gdx;
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
        this.viewport = (ScreenViewport) pane.getViewport();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if(pane == null) return;
        batch.end();

        vec = localToStageCoordinates(vec.set(0, 0));
        viewport.setScreenPosition((int)vec.x, (int)vec.y);
        viewport.setScreenSize((int)getWidth(), (int)getHeight());
        viewport.apply(false);

//        Gdx.gl.glClearColor(.3f, .3f, .3f, 1);
//        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
        pane.act(Gdx.graphics.getDeltaTime());
        pane.draw();

        this.ui.getViewport().apply(false);

        batch.begin();
    }

    @Override
    protected void sizeChanged() {
        pane.resize((int)getWidth(), (int)getHeight());
    }
}
