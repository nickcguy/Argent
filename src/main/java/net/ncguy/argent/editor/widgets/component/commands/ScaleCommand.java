package net.ncguy.argent.editor.widgets.component.commands;

import com.badlogic.gdx.math.Vector3;
import net.ncguy.argent.entity.WorldEntity;

/**
 * Created by Guy on 27/07/2016.
 */
public class ScaleCommand extends TransformCommand<Vector3> {

    public ScaleCommand(WorldEntity we) {
        super(we);
    }

    @Override
    protected void executeInternal(Vector3 target) {
        we.setLocalScale(target.x, target.y, target.z);
    }
}
