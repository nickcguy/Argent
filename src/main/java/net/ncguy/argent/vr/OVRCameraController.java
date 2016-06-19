package net.ncguy.argent.vr;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import net.ncguy.argent.Argent;

/**
 * Created by Guy on 17/06/2016.
 */
public class OVRCameraController extends CameraInputController {

    private OVRCore core;

    public OVRCameraController(Camera camera) {
        super(camera);
        this.core = Argent.vr;
    }

    @Override
    public void update() {
        camera.projection.set(core.getPerspectiveProjection());
        camera.position.set(core.getPosition());
        camera.direction.set(0, 0, -1);
        camera.up.set(0, 1, 0);
        camera.rotate(core.getOrientation());
    }
}
