package net.ncguy.argent.utils.gdx;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import net.ncguy.argent.Argent;

/**
 * Created by Guy on 18/08/2016.
 */
public class Renderer2D {

    private static Renderer2D instance = new Renderer2D();
    public static Renderer2D instance() {
        return instance;
    }

    private ShapeRenderer renderer;

    private Renderer2D() {
        renderer = new ShapeRenderer();
        Argent.addOnResize(this::onResize);
    }

    private void onResize(int w, int h) {
        if(renderer != null) {
            renderer.dispose();
            renderer = null;
        }
        renderer = new ShapeRenderer();
    }

    public void drawGrid(int x, int y, int w, int h, int cellSize) {
        int hIter = w / cellSize;
        int vIter = h / cellSize;

        renderer.begin(ShapeRenderer.ShapeType.Line);

        for(int i = 0; i < hIter; i++) {
            int xLoc = x + (cellSize * i);
            renderer.line(xLoc, y, xLoc, y+h);
        }
        for(int j = 0; j < vIter; j++) {
            int yLoc = y + (cellSize * j);
            renderer.line(x, yLoc, x+w, yLoc);
        }
        renderer.end();
    }

    public ShapeRenderer getRenderer() { return renderer; }

}
