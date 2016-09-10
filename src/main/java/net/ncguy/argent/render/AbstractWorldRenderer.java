package net.ncguy.argent.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.utils.Disposable;
import net.ncguy.argent.Argent;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.injector.InjectionModule;
import net.ncguy.argent.injector.InjectionStore;
import net.ncguy.argent.misc.FreeCamController;
import net.ncguy.argent.utils.MultiTargetFrameBuffer;
import net.ncguy.argent.world.GameWorld;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by Guy on 15/07/2016.
 */
public abstract class AbstractWorldRenderer<T extends WorldEntity> implements Disposable {

    protected GameWorld<T> world;
    protected PerspectiveCamera camera;
    protected ModelBatch modelBatch;

    protected Set<IRenderAddon> additionalRenderers;

    protected FreeCamController freeCamController;
    public final Set<IRenderAddon> separateRenderers;

    public AbstractWorldRenderer(GameWorld<T> world) {
        this.separateRenderers = new LinkedHashSet<>();
        this.world = world;
        this.additionalRenderers = new LinkedHashSet<>();
        this.freeCamController = new FreeCamController(camera());
        if(Argent.isModuleLoaded(InjectionModule.class)) {
            try {
                InjectionStore.setGlobal(this.freeCamController);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
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
            camera.far = 100;
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

    public abstract void render(ModelBatch batch, float delta);

    public void render(float delta) {
        render(batch(), delta);
    }

    public void setSize(int width, int height) {
        if(camera().viewportWidth == width && camera().viewportHeight == height) return;
        resize(width, height);
    }

    public void resize(int width, int height) {
        camera().viewportWidth = width;
        camera().viewportHeight = height;
        camera().update(true);
    }

    public MultiTargetFrameBuffer[] getMrts() {
        return new MultiTargetFrameBuffer[0];
    }
    public String[] getMrtNames() {
        return new String[0];
    }
}
