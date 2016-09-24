package net.ncguy.argent.editor.widgets.component;

import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.entity.components.physics.PhysicsComponent;

/**
 * Created by Guy on 22/09/2016.
 */
public class PhysicsWidget extends ComponentWidget<PhysicsComponent> {

    public PhysicsWidget(PhysicsComponent component) {
        super(component, "Physics");
    }

    @Override
    public void setValues(WorldEntity entity) {

    }
}
