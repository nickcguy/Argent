package net.ncguy.argent.editor.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import net.ncguy.argent.editor.EditorUI;
import net.ncguy.argent.render.AbstractWorldRenderer;

/**
 * Created by Guy on 27/07/2016.
 */
public class RenderWidget extends Widget {

//    private ScreenViewport viewport;
    private EditorUI ui;
    private AbstractWorldRenderer renderer;

    private static Vector2 vec = new Vector2();

    public RenderWidget(EditorUI ui, AbstractWorldRenderer renderer) {
        this.ui = ui;
        this.renderer = renderer;
//        this.viewport = new ScreenViewport(this.renderer.camera());
    }

    public RenderWidget(EditorUI ui) {
        this.ui = ui;
//        this.viewport = new ScreenViewport();
    }

//    public ScreenViewport getViewport() { return viewport; }
    public void setRenderer(AbstractWorldRenderer renderer) {
        this.renderer = renderer;
//        viewport.setCamera(this.renderer.camera());
    }

    // TODO disconnect camera from viewport

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if(renderer == null) return;
        batch.end();

        vec = localToStageCoordinates(vec.set(0, 0));
        Gdx.gl.glViewport((int)vec.x, (int)vec.y, (int)getWidth(), (int)getHeight());
//        viewport.setScreenPosition((int)vec.x, (int)vec.y);
//        viewport.apply(true);
        renderer.setSize((int)getWidth(), (int) getHeight());
        renderer.render(Gdx.graphics.getDeltaTime());

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

//        this.ui.getViewport().apply(true);

        batch.begin();
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
//        viewport.update((int)getWidth(), (int)getHeight());
    }
}
