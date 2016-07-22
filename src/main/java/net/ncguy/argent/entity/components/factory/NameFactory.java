package net.ncguy.argent.entity.components.factory;

import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.entity.components.NameComponent;

import java.util.ArrayList;

/**
 * Created by Guy on 22/07/2016.
 */
public class NameFactory extends ArgentComponentFactory<NameComponent> {

    @Override
    public Class<NameComponent> componentClass() {
        return NameComponent.class;
    }

    @Override
    public ArrayList<String> errors(WorldEntity entity) {
        ArrayList<String> errors = new ArrayList<>();
        error(errors, "Name component already exists in selected entity", () -> entity.getComponent(NameComponent.class) != null);
        return errors;
    }

    @Override
    public String meta() {
        return "Sets the name for an entity";
    }



    @Override
    public NameComponent build(WorldEntity entity) {
        return new NameComponent("");
    }
}
