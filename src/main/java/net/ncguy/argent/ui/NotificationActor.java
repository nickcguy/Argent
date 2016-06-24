package net.ncguy.argent.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Created by Guy on 24/06/2016.
 */
public class NotificationActor extends Dialog {

    private String text;
    private float duration;
    private int padding = 8;
    private Label label;

    public NotificationActor(String title, String text, Skin skin, float duration) {
        super(title, skin);
        this.text = text;
        this.duration = duration;
        getColor().a = 0;

        getTitleLabel().setText(title);
//        padTop(0);

        setWidth(Gdx.graphics.getWidth()*0.2f);

        label = new Label(text, skin);
        label.setWrap(true);
        addActor(label);
        label.pack();
        label.setWidth(getWidth()-getPadX());
        label.pack();
        label.setWidth(getWidth()-getPadX());
        setHeight(label.getHeight()+getPadY());
        label.setPosition(getPadLeft(), getPadBottom());
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        if(label != null) {
            label.setWrap(true);
            label.pack();
            label.setWidth(getWidth() - getPadX());
            label.pack();
            label.setWidth(getWidth() - getPadX());
            setHeight(label.getHeight() + getPadY());
            label.setPosition(getPadLeft(), getPadBottom());
        }
    }

    public NotificationActor addToStage(NotificationContainer group, Vector2 anchor) {
        group.addActor(this);
        setX((Gdx.graphics.getWidth()*anchor.x)-(getWidth() *anchor.x));
        setWidth(group.getWidth());
//        setY((screenSize.y*anchor.y)-(getHeight()*anchor.y));
        return this;
    }

    public void open() {
        addAction(Actions.fadeIn(0.3f));
        addAction(Actions.sequence(Actions.delay(this.duration), Actions.run(this::close)));
    }

    public void close() {
        addAction(Actions.sequence(Actions.fadeOut(0.3f), Actions.run(this::remove)));
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }


}
