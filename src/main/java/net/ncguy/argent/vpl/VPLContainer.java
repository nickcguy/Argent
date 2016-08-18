package net.ncguy.argent.vpl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import net.ncguy.argent.injector.ArgentInjector;
import net.ncguy.argent.injector.Inject;
import net.ncguy.argent.ui.Toaster;
import net.ncguy.argent.utils.InputManager;
import net.ncguy.argent.utils.gdx.CameraZoomController;

/**
 * Created by Guy on 18/08/2016.
 */
public class VPLContainer extends Stage {

    OrthographicCamera camera;

    Vector3 unprojectVec = new Vector3();
    CameraZoomController zoomController;
    float lastX, lastY;
    float gridSize = 65536;

    @Inject
    InputManager inputManager;
    @Inject
    Toaster toaster;

    int panKey = Input.Keys.SHIFT_LEFT;
    VPLPane pane;

    public VPLContainer() {
        super();
        init();
    }

    public VPLContainer(Viewport viewport) {
        super(viewport);
        init();
    }

    public VPLContainer(Viewport viewport, Batch batch) {
        super(viewport, batch);
        init();
    }

    private void init() {

        ArgentInjector.inject(this);
        inputManager.addProcessor(this);

        addListener(this.listener);

        setViewport(new ScreenViewport(getCamera()));
        pane = new VPLPane(this);

        camera = (OrthographicCamera) getCamera();
        zoomController = new CameraZoomController(camera, unprojectVec);

        addActor(pane);
        pane.setBounds(-(gridSize/2), -(gridSize/2), gridSize, gridSize);

    }

    public void reset() {
        resetZoom();
        camera.position.x = 0;
        camera.position.y = 0;
        toaster.info("Resetting camera");
    }

    public void resetZoom() {
        camera.zoom = 2.5f;
    }

    public void pan() {
        pan(Gdx.input.getDeltaX(), -Gdx.input.getDeltaY());
    }

    public void pan(float deltaX, float deltaY) {
        camera.position.x = camera.position.x - deltaX * camera.zoom;
        camera.position.y = camera.position.y - deltaY * camera.zoom;
    }

    public Matrix4 getCombinedMatrix() {
        return camera.combined;
    }

    public float getX() { return camera.position.x; }
    public float getY() { return camera.position.y; }
    public float getWidth() { return camera.viewportWidth * camera.zoom; }
    public float getHeight() { return camera.viewportHeight * camera.zoom; }
    public float getZoom() { return camera.zoom; }
    public void setZoom(float zoom) { camera.zoom = zoom; }

    public Vector3 unproject(Vector3 vector) {
        return getViewport().unproject(vector);
    }
    public Vector3 project(Vector3 vector) {
        return getViewport().project(vector);
    }

    public float getInputX() {
        unprojectVec.x = Gdx.input.getX();
        getViewport().unproject(unprojectVec);
        return unprojectVec.x;
    }

    public float getInputY() {
        unprojectVec.y = Gdx.graphics.getHeight() - Gdx.input.getY();
        getViewport().unproject(unprojectVec);
        return unprojectVec.y;
    }

    public void setPosition(float x, float y) {
        camera.position.set(x, y, 0);
    }

    public void update() {
        camera.update();
    }

    @Override
    public void draw() {
        super.draw();
    }

    public void resize(int width, int height) {
        getViewport().update(width, height, true);
        getCamera().update(true);

        pane.setBounds(-(gridSize/2), -(gridSize/2), gridSize, gridSize);
    }

    private InputListener listener = new InputListener() {
        @Override
        public boolean scrolled(InputEvent event, float x, float y, int amount) {
            return zoomController.zoomAroundPoint(x, y, amount);
        }

        @Override
        public boolean mouseMoved(InputEvent event, float x, float y) {
            if(Gdx.input.isKeyPressed(panKey)) {
                pan();
                return true;
            }
            return false;
        }

        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            return true;
        }

        @Override
        public void touchDragged(InputEvent event, float x, float y, int pointer) {
            if(Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) pan();
        }

        @Override
        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            if(button == Input.Buttons.MIDDLE) {
                reset();
            }
        }
    };

    public Vector2 getPosition() {
        return new Vector2(camera.position.x, camera.position.y);
    }
}