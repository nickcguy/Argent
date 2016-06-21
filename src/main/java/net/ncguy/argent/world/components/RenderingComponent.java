package net.ncguy.argent.world.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * Created by Guy on 20/06/2016.
 */
public class RenderingComponent implements Component, Poolable {

    ModelInstance instance;

    public RenderingComponent(ModelInstance instance) {
        this.instance = instance;
    }

    public ModelInstance instance() { return instance; }
    public void instance(ModelInstance instance) { this.instance = instance; }

    public void setModel(Model model) {
        this.instance.model.nodes.clear();
        this.instance.model.nodes.addAll(model.nodes);
    }

    @Override
    public void reset() {
        instance = new ModelInstance(new Model());
    }
}