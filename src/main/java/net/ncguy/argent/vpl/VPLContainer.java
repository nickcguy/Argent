package net.ncguy.argent.vpl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import net.ncguy.argent.injector.ArgentInjector;
import net.ncguy.argent.injector.Inject;
import net.ncguy.argent.ui.Toaster;

/**
 * Created by Guy on 18/08/2016.
 */
public class VPLContainer extends WidgetGroup {

    float gridSize = 65536;
    Vector2 panePosition;

    @Inject
    Toaster toaster;

    int panKey = Input.Keys.SHIFT_LEFT;
    public VPLPane pane;
    private String[] tags;

    public VPLContainer(String... tags) {
        super();
        this.tags = tags;
        init();
    }

    private void init() {

        ArgentInjector.inject(this);

        addListener(this.listener);

        pane = new VPLPane(this, this.tags);

        addActor(pane);
        pane.setSize(gridSize, gridSize);
        panePosition = new Vector2(-(gridSize/2), -(gridSize/2));
        pane.setPosition(panePosition.x, panePosition.y);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        toBack();
        super.draw(batch, parentAlpha);
    }

    private InputListener listener = new InputListener() {
        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            return button == Input.Buttons.LEFT;
        }

        @Override
        public void touchDragged(InputEvent event, float x, float y, int pointer) {
//            if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                pane.moveBy(Gdx.input.getDeltaX(), -Gdx.input.getDeltaY());
                panePosition.set(pane.getX(), pane.getY());
//            }
        }
    };

    public Vector2 getPosition() {
        return panePosition.cpy().add(gridSize/2, gridSize/2);
    }
}