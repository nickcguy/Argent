package net.ncguy.argent.ui;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.VisUI;
import net.ncguy.argent.Argent;

/**
 * Created by Guy on 16/09/2016.
 */
public class MutableFloatSlider extends Slider {

    private final MutableFloat target;

    public MutableFloatSlider(MutableFloat target, float min, float max, float step, boolean vertical) {
        super(min, max, step, vertical, VisUI.getSkin());
        this.target = target;
        setValue(target.floatValue());
        attachListeners();
    }

    private void attachListeners() {
        addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                propagateToTarget();
            }
        });
    }

    public void propagateToTarget() {
        propagateToTarget(true);
    }

    public void propagateToTarget(boolean tween) {
        if(tween) tweenTarget(getVisualValue());
        else setTarget(getVisualValue());
    }

    private void tweenTarget(float val) {
        if(Argent.tween == null) setTarget(val);
        else Tween.to(target, 0, .3f).target(val).start(Argent.tween);
    }

    private void setTarget(float val) {
        target.setValue(val);
    }

}
