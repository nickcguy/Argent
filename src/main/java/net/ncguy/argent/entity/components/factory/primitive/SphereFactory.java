package net.ncguy.argent.entity.components.factory.primitive;

import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.entity.components.factory.ArgentComponentFactory;
import net.ncguy.argent.entity.components.primitive.SphereComponent;

import java.util.ArrayList;

/**
 * Created by Guy on 24/07/2016.
 */
public class SphereFactory extends ArgentComponentFactory<SphereComponent> {

    @Override
    public Class<SphereComponent> componentClass() {
        return SphereComponent.class;
    }

    @Override
    public ArrayList<String> errors(WorldEntity entity) {
        ArrayList<String> errors = new ArrayList<>();
        error(errors, "A Sphere component is already attached to the entity", () -> entity.getComponent(SphereComponent.class) != null);
        return errors;
    }

    @Override
    public String meta() {
        return "";
    }

    @Override
    public SphereComponent build(WorldEntity entity) {
        SphereComponent comp = new SphereComponent();
        comp.parent(entity);
        return comp;
    }

}
