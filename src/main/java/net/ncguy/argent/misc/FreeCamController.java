package net.ncguy.argent.misc;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Guy on 19/06/2016.
 */
public class FreeCamController extends CameraInputController {

    public static final float SPEED_01 = 1f;
    public static final float SPEED_1 = 10f;
    public static final float SPEED_10 = 100f;

    public FreeCamController(Camera camera) {
        super(camera);
    }

    public void setVelocity(float velocity) { this.translateUnits = velocity; }

    public Vector3 cameraPosition() {
        return camera.position;
    }


    public void setCamera(PerspectiveCamera camera) {
        this.camera = camera;
    }
}
