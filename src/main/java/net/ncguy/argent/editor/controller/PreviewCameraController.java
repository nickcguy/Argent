package net.ncguy.argent.editor.controller;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Guy on 22/07/2016.
 */
public class PreviewCameraController extends CameraInputController {

    protected boolean active = false;

    public PreviewCameraController(Camera camera) {
        super(camera);
    }

    public void reset() {
        camera.lookAt(target);
        camera.position.set(target.cpy().add(5));
        camera.lookAt(target);
        camera.position.set(target.cpy().add(5));
        camera.up.set(0, 1, 0);

    }

    public void focusOn(Vector3 point) {
        this.target.set(point);
    }

    public void disable() {
//        if(!active) return;
//        active = false;
//        AppUtils.Input.addInputProcessor(this);
    }

    public void enable() {
//        if(active) return;
//        active = true;
//        AppUtils.Input.removeInputProcessor(this);
    }

}
