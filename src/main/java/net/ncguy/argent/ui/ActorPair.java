package net.ncguy.argent.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;

/**
 * Created by Guy on 11/07/2016.
 */
public class ActorPair<T extends Actor, U extends Actor> extends Group {

    private T left;
    private U right;

    private float lWidth, rWidth;

    public ActorPair(T left, U right) {
        this.left = left;
        this.right = right;

        this.lWidth = this.left.getWidth();
        this.rWidth = this.right.getWidth();

        addActor(left);
        addActor(right);
    }

    public float lWidth() { return lWidth; }
    public ActorPair<T, U> lWidth(float lWidth) { this.lWidth = lWidth; return this; }
    public float rWidth() { return rWidth; }
    public ActorPair<T, U> rWidth(float rWidth) { this.rWidth = rWidth; return this; }

    @Override
    public void draw(Batch batch, float parentAlpha) {
//        this.left.setWidth(lWidth());
//        this.left.setX(0);
//        this.right.setWidth(rWidth());
//        this.right.setX(lWidth()+2);
        super.draw(batch, parentAlpha);
    }
}
