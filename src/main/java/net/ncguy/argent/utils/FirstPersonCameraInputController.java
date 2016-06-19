package net.ncguy.argent.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntIntMap;

import static com.badlogic.gdx.Gdx.input;
import static com.badlogic.gdx.Input.Keys.*;

/**
 * Created by Guy on 19/06/2016.
 */
public class FirstPersonCameraInputController extends CameraInputController {

    private final IntIntMap keys = new IntIntMap();
    private float velocity = 25;
    private float degreesPerPixel = .5f;
    private final Vector3 tmp = new Vector3();

    private int strafeLeftKey = A, strafeRightKey = D;
    private int ascendKey = Q, descendKey = Z;
    private int fasterKey = SHIFT_LEFT;

    public FirstPersonCameraInputController(Camera camera) {
        super(camera);
        defaultKeys();
    }

    protected FirstPersonCameraInputController(CameraGestureListener gestureListener, Camera camera) {
        super(gestureListener, camera);
        defaultKeys();
    }

    public void defaultKeys() {
        forwardKey = W;
        backwardKey = S;
        strafeLeftKey = A;
        strafeRightKey = D;
        ascendKey = Q;
        descendKey = Z;
    }

    @Override
    public boolean keyDown(int keycode) {
        keys.put(keycode, keycode);
        return super.keyDown(keycode);
    }

    @Override
    public boolean keyUp(int keycode) {
        keys.remove(keycode, 0);
        return super.keyUp(keycode);
    }

    public void setVelocity(float velocity) { this.velocity = velocity; }
    public void setDegreesPerPixel(float degreesPerPixel) { this.degreesPerPixel = degreesPerPixel; }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        float deltaX = -input.getDeltaX() * degreesPerPixel;
        float deltaY = -input.getDeltaY() * degreesPerPixel;
        camera.direction.rotate(camera.up, deltaX);
        tmp.set(camera.direction).crs(camera.up).nor();
        camera.direction.rotate(tmp, deltaY);
        return true;
//        return super.touchDragged(screenX, screenY, pointer);
    }

    @Override
    public void update() {
        update(Gdx.graphics.getDeltaTime());
    }

    public void update(float delta) {

        float velocity = this.velocity;

        if(keys.containsKey(fasterKey))
            velocity *= 5;

        camera.up.set(0, 1, 0);

        if(keys.containsKey(forwardKey)) {
            tmp.set(camera.direction).nor().scl(delta * velocity);
            camera.position.add(tmp);
        }
        if(keys.containsKey(backwardKey)) {
            tmp.set(camera.direction).nor().scl(-delta * velocity);
            camera.position.add(tmp);
        }
        if(keys.containsKey(strafeLeftKey)) {
            tmp.set(camera.direction).crs(camera.up).nor().scl(-delta * velocity);
            camera.position.add(tmp);
        }
        if(keys.containsKey(strafeRightKey)) {
            tmp.set(camera.direction).crs(camera.up).nor().scl(delta * velocity);
            camera.position.add(tmp);
        }
        if(keys.containsKey(ascendKey)) {
            tmp.set(camera.up).nor().scl(delta * velocity);
            camera.position.add(tmp);
        }
        if(keys.containsKey(descendKey)) {
            tmp.set(camera.up).nor().scl(-delta * velocity);
            camera.position.add(tmp);
        }
        camera.update(true);
    }

}
