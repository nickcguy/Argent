package net.ncguy.argent.entity.components.factory;

import net.ncguy.argent.data.Meta;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.entity.components.RenderableComponent;

import java.util.ArrayList;

/**
 * Created by Guy on 22/07/2016.
 */
@Meta(displayName = "Renderable", category = "Component")
public class RenderableFactory extends ArgentComponentFactory<RenderableComponent> {

    @Override
    public Class<RenderableComponent> componentClass() {
        return RenderableComponent.class;
    }

    @Override
    public ArrayList<String> errors(WorldEntity entity) {
        ArrayList<String> errors = new ArrayList<>();
        error(errors, name()+" component already exists in selected entity", () -> entity.getComponent(componentClass()) != null);
        return errors;
    }

    @Override
    public String meta() {
        return "Provides a model instance for rendering";
    }


    @Override
    public RenderableComponent build(WorldEntity entity) {
        RenderableComponent renderable = new RenderableComponent();
        renderable.parent(entity);
        return renderable;
    }
}
