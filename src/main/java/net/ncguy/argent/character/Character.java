package net.ncguy.argent.character;

import com.badlogic.gdx.math.Vector3;
import net.ncguy.argent.character.controller.CharacterController;
import net.ncguy.argent.core.VarRunnables;

/**
 * Created by Guy on 13/07/2016.
 */
public abstract class Character {

    protected CharacterController controller;

    public CharacterController controller() { return controller; }
    public Character Controller(CharacterController controller) {
        this.controller = controller;
        return this;
    }

    public VarRunnables.VarRunnable<CharacterController> onPossess, onUnPossess;

    public Character onPossess(CharacterController controller) {
        this.controller = controller;
        if(onPossess != null) onPossess.run(controller);
        return this;
    }

    public Character onUnPossess() {
        if(onUnPossess != null) onUnPossess.run(this.controller);
        this.controller = null;
        return this;
    }

    public abstract void move(Vector3 dir, float speed);
    public abstract void rotate(Vector3 flatForwardVector);

    public Vector3 getTranslation() {
        return Vector3.Zero;
    }
}
