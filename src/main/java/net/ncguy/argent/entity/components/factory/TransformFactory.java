package net.ncguy.argent.entity.components.factory;

import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.entity.components.TransformComponent;

import java.util.ArrayList;

/**
 * Created by Guy on 22/07/2016.
 */
public class TransformFactory extends ArgentComponentFactory<TransformComponent> {

    @Override
    public String meta() {
        return "Provides a Matrix4 for the entity to position itself within the world. All entities have this component.";
    }

    @Override
    public Class<TransformComponent> componentClass() {
        return TransformComponent.class;
    }

    @Override
    public ArrayList<String> errors(WorldEntity entity) {
        ArrayList<String> errors = new ArrayList<>();
        error(errors, "Transform component already exists in selected entity", () -> entity.getComponent(TransformComponent.class) != null);
        error(errors, "Transform component cannot be removed from an entity", () -> true);
        return errors;
    }

    @Override
    public boolean canBeRemoved(WorldEntity entity) {
        return false;
    }

    @Override
    public TransformComponent build(WorldEntity entity) {
        return null;
    }
}
