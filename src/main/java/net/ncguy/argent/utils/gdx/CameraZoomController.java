package net.ncguy.argent.utils.gdx;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Guy on 18/08/2016.
 */
public class CameraZoomController {

    private final OrthographicCamera camera;
    private Vector3 unprojectVec;

    public CameraZoomController(OrthographicCamera camera, Vector3 unprojectVec) {
        this.camera = camera;
        this.unprojectVec = unprojectVec;
    }

    public boolean zoomAroundPoint(float x, float y, int amount) {
        float newZoom = 0;
        camera.unproject(unprojectVec.set(x, y, 0));
        float cursorX = unprojectVec.x;
        float cursorY = unprojectVec.y;
        if(amount == -1) { // Zoom in
            if(camera.zoom <= .3f) return false;
            newZoom = camera.zoom - .1f * camera.zoom * 2;
        }else if(amount == 1) { // Zoom out
            if(camera.zoom >= 15f) return false;
            newZoom = camera.zoom + .1f * camera.zoom * 2;
        }

//        camera.position.x = cursorX + (newZoom / camera.zoom) * (camera.position.x - cursorX);
//        camera.position.y = cursorY + (newZoom / camera.zoom) * (camera.position.y - cursorY);
        camera.zoom = newZoom;

        return true;
    }

}
