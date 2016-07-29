package net.ncguy.argent.editor.widgets.component.commands;

import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.entity.components.LightComponent;

/**
 * Created by Guy on 29/07/2016.
 */
public class LinearCommand extends TransformCommand<Float> {

    public LinearCommand(WorldEntity we) {
        super(we);
    }

    @Override
    protected void executeInternal(Float target) {
        if(we.has(LightComponent.class)) {
            LightComponent light = we.get(LightComponent.class);
            light.setLinear(target);
        }
    }

}
