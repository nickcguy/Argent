package net.ncguy.argent.world;

import net.ncguy.argent.render.WorldRenderer;

import java.util.List;

/**
 * Created by Guy on 13/06/2016.
 */
public class GameWorld<T> {

    private WorldRenderer<T> renderer;
    private List<T> instances;

    public GameWorld(WorldRenderer<T> renderer, List<T> instances) {
        this.instances = instances;
        this.renderer = renderer;
    }

    public void addInstance(T instance)    { this.instances.add(instance);    }
    public void removeInstance(T instance) { this.instances.remove(instance); }

    public void render(float delta) { renderer.render(delta); }

}
