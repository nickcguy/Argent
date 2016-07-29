package net.ncguy.argent.editor.widgets.component.commands;

import com.badlogic.gdx.graphics.Color;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.entity.components.LightComponent;

/**
 * Created by Guy on 29/07/2016.
 */
public class ColourCommand extends TransformCommand<Color> {

    public ColourCommand(WorldEntity we) {
        super(we);
    }

    @Override
    protected void executeInternal(Color target) {
        if(we.has(LightComponent.class)) {
            LightComponent light = we.get(LightComponent.class);
            light.getColour().set(target);
        }
    }
}
