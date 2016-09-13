package net.ncguy.argent.render.scene;

import com.badlogic.gdx.Gdx;
import net.ncguy.argent.Argent;
import net.ncguy.argent.editor.EditorUI;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.event.SceneGraphChangedEvent;
import net.ncguy.argent.event.WorldEntitySelectedEvent;
import net.ncguy.argent.injector.ArgentInjector;
import net.ncguy.argent.injector.Inject;
import net.ncguy.argent.render.AbstractWorldRenderer;
import net.ncguy.argent.render.argent.ArgentRenderer;
import net.ncguy.argent.world.GameWorld;

import java.util.List;

/**
 * Created by Guy on 27/07/2016.
 */
public class SceneGraph {

    protected GameWorld<WorldEntity> world;

    public Scene scene;
    public AbstractWorldRenderer renderer;
    @Inject private EditorUI editorUI;
    private WorldEntity selected;

    public SceneGraph(Scene scene) {
        this.world = new GameWorld<WorldEntity>() {
            @Override
            public WorldEntity buildInstance() {
                return new WorldEntity();
            }
        };
        this.scene = scene;
//        this.renderer = new ShaderWorldRenderer(this.world);
        this.renderer = new ArgentRenderer(this.world);
//        this.renderer = new BasicWorldRenderer(this.world);
    }

    private int injectionAttempt = 0;
    private int injectionAttemptMax = 3;

    public EditorUI editorUI() {
        if(editorUI == null) {
            if(injectionAttempt > injectionAttemptMax) throw new RuntimeException("Unable to get the EditorUI instance through injection");
            injectionAttempt++;
            ArgentInjector.inject(this);
        }
        return editorUI;
    }

    public void render() {
        render(Gdx.graphics.getDeltaTime());
    }
    public void render(float delta) {
        EditorUI ui = editorUI();
        if(ui != null) ui.draw();
//        renderer.render(delta);
        world.instances().forEach(i -> i.render(delta));
    }

    public void update() {
        update(Gdx.graphics.getDeltaTime());
    }
    public void update(float delta) {
        EditorUI ui = editorUI();
        if(ui != null) ui.act(delta);
        world.update(delta);
    }

    public List<WorldEntity> getWorldEntities() {
        return world.instances();
    }
    public void addWorldEntity(WorldEntity entity) {
        world.addInstance(entity);
        Argent.event.post(new SceneGraphChangedEvent());
    }
    public void removeWorldEntity(WorldEntity entity) {
        world.removeInstance(entity);
    }

    public WorldEntity getSelected() {
        return selected;
    }

    public void setSelected(WorldEntity selected) {
        this.selected = selected;
        Argent.event.post(new WorldEntitySelectedEvent(selected));
    }

    public void renderPickableScene() {

    }
}
