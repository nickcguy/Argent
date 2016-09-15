package net.ncguy.argent.editor.widgets.component.commands;

import com.badlogic.gdx.math.Vector3;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.entity.components.light.LightComponent;

/**
 * Created by Guy on 29/07/2016.
 */
public class LightTranslateCommand extends TransformCommand<Vector3> {

    public LightTranslateCommand(WorldEntity we) {
        super(we);
    }

    @Override
    protected void executeInternal(Vector3 target) {
        if(we.has(LightComponent.class)) {
            LightComponent light = we.get(LightComponent.class);
            light.getLocalPosition().set(target);
        }
    }
}
