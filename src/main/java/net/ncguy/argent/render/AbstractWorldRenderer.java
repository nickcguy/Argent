package net.ncguy.argent.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import net.ncguy.argent.world.GameWorld;

/**
 * Created by Guy on 15/07/2016.
 */
public abstract class AbstractWorldRenderer<T extends RenderableProvider> {

    protected GameWorld<T> world;
    protected PerspectiveCamera camera;
    protected ModelBatch modelBatch;

    public AbstractWorldRenderer(GameWorld<T> world) {
        this.world = world;
    }

    public GameWorld<T> world() { return world; }

    public PerspectiveCamera camera() {
        if(camera == null) {
            camera = new PerspectiveCamera(90, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            camera.near = .1f;
            camera.far = 1000;
            camera.position.set(10, 0, 0);
            camera.lookAt(0, 0, 0);
        }
        return camera;
    }

    public ModelBatch batch() {
        if(modelBatch == null) {
            modelBatch = new ModelBatch();
        }
        return modelBatch;
    }

    public abstract void render(float delta);

}
