package net.ncguy.argent.editor.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import net.ncguy.argent.editor.EditorUI;
import net.ncguy.argent.render.AbstractWorldRenderer;

/**
 * Created by Guy on 27/07/2016.
 */
public class RenderWidget extends Widget implements Disposable {

    private ScreenViewport viewport;
    private EditorUI ui;
    private AbstractWorldRenderer renderer;

    private static Vector2 vec = new Vector2();

    public RenderWidget(EditorUI ui, AbstractWorldRenderer renderer) {
        this.ui = ui;
        this.renderer = renderer;
        this.viewport = new ScreenViewport(this.renderer.camera());
    }

    public RenderWidget(EditorUI ui) {
        this.ui = ui;
        this.viewport = new ScreenViewport();
    }

    public ScreenViewport getViewport() { return viewport; }
    public void setRenderer(AbstractWorldRenderer renderer) {
        this.renderer = renderer;
        viewport.setCamera(this.renderer.camera());
    }

    public AbstractWorldRenderer getRenderer() {
        return renderer;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if(renderer == null) return;
        batch.end();

        vec = localToStageCoordinates(vec.set(0, 0));
        viewport.setScreenPosition((int)vec.x, (int)vec.y);
        viewport.setScreenSize((int)getWidth(), (int)getHeight());
        viewport.apply(false);
        renderer.setSize((int)getWidth(), (int) getHeight());
        renderer.render(Gdx.graphics.getDeltaTime());


        this.ui.getViewport().apply(false);

        batch.begin();
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
//        viewport.update((int)getWidth(), (int)getHeight());
    }

    @Override
    public void dispose() {
        if(renderer != null)
            renderer.dispose();
    }
}
