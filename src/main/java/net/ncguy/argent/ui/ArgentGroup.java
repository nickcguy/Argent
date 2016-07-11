package net.ncguy.argent.ui;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

/**
 * Created by Guy on 11/07/2016.
 */
public class ArgentGroup extends WidgetGroup {

    private boolean isOpen = true;
    private final boolean vertical;
    private float targetSize = 100;
    private float tweenDuration = .3f;
    private Interpolation interpolation = Interpolation.linear;

    public ArgentGroup(boolean vertical) {
        this.vertical = vertical;
        setFillParent(true);
    }

    public float tweenDuration() { return tweenDuration; }
    public ArgentGroup tweenDuration(float tweenDuration) {
        this.tweenDuration = tweenDuration;
        return this;
    }

    public Interpolation interpolation() { return interpolation; }
    public ArgentGroup interpolation(Interpolation interpolation) {
        this.interpolation = interpolation;
        return this;
    }

    public void collapse() {
        update(false);
    }

    public void expand() {
        update(true);
    }

    public void toggle() {
        update(!isOpen);
    }

    public void setExpanded(boolean expanded) {
        update(expanded);
    }

    private void update(boolean newState) {
        if(isOpen == newState) return;
        isOpen = newState;
        update();
    }

    private void update() {
        clearActions();
        float target = targetSize;
        if(!isOpen) target = 0;
        Action action;
        if(vertical) action = Actions.sizeTo(getX(), target, tweenDuration, interpolation);
        else action = Actions.sizeTo(target, getY(), tweenDuration, interpolation);
        addAction(Actions.parallel(
                Actions.alpha(isOpen ? 1 : 0, tweenDuration, interpolation),
                action
        ));
    }

    // OVERRIDES

    @Override
    public void setSize(float width, float height) {
        if(vertical) targetSize = height;
        else targetSize = width;
        if(!isOpen) {
            if(vertical) height = 0;
            else width = 0;
        }
        super.setSize(width, height);
    }

    @Override
    public void sizeBy(float size) {
        targetSize = size;
        if(!isOpen) {
            if(vertical) super.sizeBy(0, size);
            else super.sizeBy(size, 0);
        }else super.sizeBy(size);
    }

    @Override
    public void sizeBy(float width, float height) {
        if(vertical) targetSize += height;
        else targetSize += width;
        if(!isOpen) {
            if(vertical) height = 0;
            else width = 0;
        }
        super.sizeBy(width, height);
    }

    @Override
    public void setBounds(float x, float y, float width, float height) {
        if(vertical) targetSize = height;
        else targetSize = width;
        if(!isOpen) {
            if(vertical) height = 0;
            else width = 0;
        }
        super.setBounds(x, y, width, height);
    }
}
