package net.ncguy.argent.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.world.GameWorld;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by Guy on 15/07/2016.
 */
public abstract class AbstractWorldRenderer<T extends WorldEntity> {

    protected GameWorld<T> world;
    protected PerspectiveCamera camera;
    protected ModelBatch modelBatch;

    protected Set<IRenderAddon> additionalRenderers;

    public AbstractWorldRenderer(GameWorld<T> world) {
        this.world = world;
        this.additionalRenderers = new LinkedHashSet<>();
    }

    public void addRenderer(IRenderAddon addon) {
        this.additionalRenderers.add(addon);
    }
    public void removeRenderer(IRenderAddon addon) {
        this.additionalRenderers.remove(addon);
    }

    public GameWorld<T> world() { return world; }

    public PerspectiveCamera camera() {
        if(camera == null) {
            camera = new PerspectiveCamera(90, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            camera.near = .1f;
            camera.far = 1000;
            camera.position.set(3, 3, 3);
            camera.lookAt(0, 0, 0);
        }
        camera.update(true);
        return camera;
    }

    public ModelBatch batch() {
        if(modelBatch == null) modelBatch = new ModelBatch();
        return modelBatch;
    }

    public abstract void render(float delta);

    public void resize(int width, int height) {
        camera().viewportWidth = width;
        camera().viewportHeight = height;
        camera().update(true);
    }
}
