package net.ncguy.argent.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.ArrayList;
import java.util.List;

import static net.ncguy.argent.core.VarRunnables.*;

/**
 * Created by Guy on 12/06/2016.
 */
public class MaterialWrapper extends Actor {

    private Actor actor;
    private List<Var2Runnable<Float>> changeListeners_pos;
    private List<Var2Runnable<Float>> changeListeners_dim;
    private List<VarRunnable<Float>> changeListeners_rot;

    public MaterialWrapper(Actor actor) {
        this.actor = actor;
        changeListeners_pos = new ArrayList<>();
        changeListeners_dim = new ArrayList<>();
        changeListeners_rot = new ArrayList<>();
        addListener(new ClickListener(){

        });
    }

    public void addPositionListener(Var2Runnable<Float> listener) { changeListeners_pos.add(listener); }
    public void removePositionListener(Var2Runnable<Float> listener) { changeListeners_pos.remove(listener); }

    public void addSizeListener(Var2Runnable<Float> listener) { changeListeners_dim.add(listener); }
    public void removeSizeListener(Var2Runnable<Float> listener) { changeListeners_dim.remove(listener); }

    public void addRotationListener(VarRunnable<Float> listener) { changeListeners_rot.add(listener); }
    public void removeRotationListener(VarRunnable<Float> listener) { changeListeners_rot.remove(listener); }

    public Actor actor() { return actor; }
    public void actor(Actor actor) { this.actor = actor; }

    @Override
    protected void positionChanged() {
        super.positionChanged();
        if(this.actor != null)
            this.actor.setPosition(getX(), getY());
        changeListeners_pos.forEach(r -> r.run(getX(), getY()));
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        if(this.actor != null)
            this.actor.setSize(getWidth(), getHeight());
        changeListeners_dim.forEach(r -> r.run(getWidth(), getHeight()));
    }

    @Override
    protected void rotationChanged() {
        super.rotationChanged();
        if(this.actor != null)
            this.actor.setRotation(getRotation());
        changeListeners_rot.forEach(r -> r.run(getRotation()));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if(this.actor == null) return;
        this.actor.draw(batch, parentAlpha);
    }
}
