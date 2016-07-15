package net.ncguy.argent.character.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import net.ncguy.argent.Argent;
import net.ncguy.argent.character.Character;
import net.ncguy.argent.utils.GdxUtils;

import static com.badlogic.gdx.Gdx.input;
import static com.badlogic.gdx.Input.Keys.*;

/**
 * Created by Guy on 13/07/2016.
 * <br><br>
 * Basic character controller implementation, allowing for basic movement
 *
 */
public class PlayerController extends CharacterController implements InputProcessor {

    // TODO integrate into camera positioning properly
    public final Vector3 cameraOffset = new Vector3();
    private final Vector3 tmp = new Vector3();
    public Camera camera;

    public float degreesPerPixel = .5f;

    public float speed = 250;

    public int move_forward = 0;
    public int move_backward = 0;
    public int strafe_left = 0;
    public int strafe_right = 0;

    protected boolean toMoveForward = false;
    protected boolean toMoveBackward = false;
    protected boolean toStrafeLeft = false;
    protected boolean toStrafeRight = false;

    public void defaultKeys() {
        move_forward = W;
        move_backward = S;
        strafe_left = A;
        strafe_right = D;
    }

    public PlayerController(Camera camera) {
        this(camera, null);
    }

    public PlayerController(Camera camera, Character character) {
        super(character);
        this.camera = camera;
        defaultKeys();
    }

    public Vector3 cameraOffset() { return cameraOffset; }

    public void setCameraOffset(Vector3 offset) {
        cameraOffset().set(offset);
    }

    public void setCameraOffset(float x, float y, float z) {
        cameraOffset().set(x, y, z);
    }

    public void attachInputProcessor() {
        attachInputProcessor(true);
    }

    public void attachInputProcessor(boolean main) {
        if(main) {
            Argent.postRunnable(() -> Argent.attachInputProcessor(this));
        }else{
            Argent.attachInputProcessor(this);
        }
    }

    @Override
    public void update() {
        if(character != null) {
            Vector3 moveVec = new Vector3();
            if (toMoveForward) moveVec.add(camera.direction);
            if (toMoveBackward) moveVec.sub(camera.direction);
            if (toStrafeLeft) moveVec.sub(GdxUtils.getRightVector(camera));
            if (toStrafeRight) moveVec.add(GdxUtils.getRightVector(camera));
            character.move(moveVec, speed* Gdx.graphics.getDeltaTime());
            character.rotate(GdxUtils.getFlatForwardVector(camera.direction));
        }
        updateCamera();
        super.update();
    }

    protected void updateCamera() {
        cameraOffset.y = 400;
        cameraOffset.z = 0;
        if(character != null) {
            camera.position.set(character.getTranslation().add(cameraOffset));
        }
        camera.update(true);
    }

    // INPUT

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == move_forward) toMoveForward = true;
        if(keycode == move_backward) toMoveBackward = true;
        if(keycode == strafe_left) toStrafeLeft = true;
        if(keycode == strafe_right) toStrafeRight = true;
        if(keycode == Input.Keys.CONTROL_LEFT) {
            Gdx.input.setCursorPosition(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
            Gdx.input.setCursorCatched(true);
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if(keycode == move_forward) toMoveForward = false;
        if(keycode == move_backward) toMoveBackward = false;
        if(keycode == strafe_left) toStrafeLeft = false;
        if(keycode == strafe_right) toStrafeRight = false;
        if(keycode == Input.Keys.CONTROL_LEFT) {
            Gdx.input.setCursorCatched(false);
            Gdx.input.setCursorPosition(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        float deltaX = -input.getDeltaX() * degreesPerPixel;
        float deltaY = -input.getDeltaY() * degreesPerPixel;
        camera.direction.rotate(camera.up, deltaX);
        tmp.set(camera.direction).crs(camera.up).nor();
        camera.direction.rotate(tmp, deltaY);
        return true;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

}
