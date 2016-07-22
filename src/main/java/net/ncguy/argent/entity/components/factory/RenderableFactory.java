package net.ncguy.argent.entity.components.factory;

import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.entity.components.RenderableComponent;

import java.util.ArrayList;

/**
 * Created by Guy on 22/07/2016.
 */
public class RenderableFactory extends ArgentComponentFactory<RenderableComponent> {

    @Override
    public Class<RenderableComponent> componentClass() {
        return RenderableComponent.class;
    }

    @Override
    public ArrayList<String> errors(WorldEntity entity) {
        ArrayList<String> errors = new ArrayList<>();
        error(errors, "Renderable component already exists in selected entity", () -> entity.getComponent(RenderableComponent.class) != null);
        return errors;
    }

    @Override
    public String meta() {
        return "Provides a model instance for rendering";
    }


    @Override
    public RenderableComponent build(WorldEntity entity) {
        RenderableComponent renderable = new RenderableComponent();
        renderable.instance().transform = entity.transform();
        return renderable;
    }
}
