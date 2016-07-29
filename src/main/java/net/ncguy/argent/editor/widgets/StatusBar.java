package net.ncguy.argent.editor.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.Separator;
import net.ncguy.argent.injector.ArgentInjector;
import net.ncguy.argent.injector.Inject;
import net.ncguy.argent.misc.FreeCamController;
import net.ncguy.argent.utils.StringUtils;

/**
 * Created by Guy on 27/07/2016.
 */
public class StatusBar extends Table {

    protected Table root, left, right;

    protected Label fpsLabel, camPos;

    protected TextButton speed01, speed1, speed10;

    @Inject
    protected FreeCamController freeCamController;

    public StatusBar() {
        super(VisUI.getSkin());
        ArgentInjector.inject(this);
        setBackground(VisUI.getSkin().getDrawable("menu-bg"));
        root = new Table(VisUI.getSkin());
        root.align(Align.left | Align.center);
        add(root).expand().fill();

        left = new Table(VisUI.getSkin());
        left.align(Align.left);
        left.padLeft(10);

        right = new Table(VisUI.getSkin());
        right.align(Align.right);
        right.padRight(10);

        root.add(left).left().expand().fill();
        root.add(right).right().expand().fill();

        // Left
        left.add("CamSpeed: ").left();
        left.add(speed01 = new TextButton(".1", VisUI.getSkin()));
        left.add(speed1 = new TextButton("1", VisUI.getSkin()));
        left.add(speed10 = new TextButton("10", VisUI.getSkin()));

        // Right
        right.add(camPos = new Label("", VisUI.getSkin())).right();
        right.add(new Separator()).expandY().fillY().padLeft(5).padRight(5);
        right.add(fpsLabel = new Label("", VisUI.getSkin())).right();

        setupListeners();
    }

    public void setupListeners() {
        speed01.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                freeCamController.setVelocity(FreeCamController.SPEED_01);
            }
        });
        speed1.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                freeCamController.setVelocity(FreeCamController.SPEED_1);
            }
        });
        speed10.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                freeCamController.setVelocity(FreeCamController.SPEED_10);
            }
        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta) {
        setFps(Gdx.graphics.getFramesPerSecond());
        setCamPos(freeCamController.cameraPosition());
        super.act(delta);
    }

    private void setFps(int fps) { this.fpsLabel.setText("FPS: "+fps); }
    private void setCamPos(Vector3 pos) {
        camPos.setText("camPos: "+ StringUtils.formatVector3(pos, 2, true));
    }

}
