package net.ncguy.argent.editor.widgets.component.commands;

import com.badlogic.gdx.math.Vector3;
import net.ncguy.argent.entity.WorldEntity;

/**
 * Created by Guy on 27/07/2016.
 */
public class TranslateCommand extends TransformCommand<Vector3> {

    public TranslateCommand(WorldEntity we) {
        super(we);
    }

    protected void executeInternal(Vector3 target) {
        we.setLocalPosition(target.x, target.y, target.z);
    }

}
