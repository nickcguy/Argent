package net.ncguy.argent.render.scene;

import com.badlogic.gdx.utils.Disposable;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.event.WorldEntitySelectedEvent;

/**
 * Created by Guy on 27/07/2016.
 */
public class Scene implements Disposable, WorldEntitySelectedEvent.WorldEntitySelectedListener {

    private String name;
    private long id;

    public SceneGraph sceneGraph;
    public Skybox skybox;


    public Scene() {
        sceneGraph = new SceneGraph(this);
    }

    @Override
    public void dispose() {
        if(skybox != null)
            skybox.dispose();
    }

    public WorldEntity selected() {
        return sceneGraph.getSelected();
    }

    public void select(WorldEntity entity) {
        sceneGraph.setSelected(entity);
    }

    @Override
    public void onWorldEntitySelected(WorldEntitySelectedEvent event) {
        select(event.getEntity());
    }
}
