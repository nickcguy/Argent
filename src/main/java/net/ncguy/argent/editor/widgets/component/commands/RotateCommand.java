package net.ncguy.argent.editor.widgets.component.commands;

import com.badlogic.gdx.math.Quaternion;
import net.ncguy.argent.entity.WorldEntity;

/**
 * Created by Guy on 27/07/2016.
 */
public class RotateCommand extends TransformCommand<Quaternion> {

    public RotateCommand(WorldEntity we) {
        super(we);
    }

    protected void executeInternal(Quaternion target) {
        we.setLocalRotation(target.x, target.y, target.z, target.w);
    }
}
