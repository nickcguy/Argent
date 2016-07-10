package net.ncguy.argent.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Created by Guy on 13/06/2016.
 */
public class BufferWidget extends Group {

    public static int desiredWidth = 256;
    Sprite sprite;
    Label label;
    Image image;
    Rectangle globalBounds;
    private boolean active;

    public Label label() { return label; }

    public String getLabelText() {
        return label().getText().toString();
    }

    public BufferWidget(String buffer, Skin skin) {
        this.label = new Label(buffer, skin);
        this.image = new Image();
        this.addActor(this.label);
        this.addActor(this.image);
        this.globalBounds = new Rectangle();
        this.active = true;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
        if(desiredWidth <= 0) desiredWidth = 1;
        float factor = this.sprite.getWidth()/desiredWidth;
        if(factor <= 0) factor = 1;
        this.setSize(desiredWidth, this.sprite.getHeight()/factor);
    }
    public void setSprite(Texture texture) {
        if(this.sprite == null) this.sprite = new Sprite(texture);
        else this.sprite.setTexture(texture);
    }

    @Override
    protected void positionChanged() {
        super.positionChanged();
        this.label.setPosition(2, getHeight()-(label.getHeight()+2));
        this.globalBounds.setPosition(localToStageCoordinates(new Vector2()));
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        this.label.setPosition(2, getHeight()-(label.getHeight()+2));
        this.globalBounds.setSize(getWidth(), getHeight());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if(!active) return;
        if(sprite != null) if(sprite.getTexture() != null) {
            positionChanged();
            sprite.setBounds(this.globalBounds.getX(),this.globalBounds.getY(), this.globalBounds.getWidth(), this.globalBounds.getHeight());
            sprite.setFlip(false, true);
            sprite.draw(batch, parentAlpha);
//            this.image.setDrawable(sprite.getTexture());
//            this.image.setBounds(getX(), getY(), getWidth(), getHeight());
        }
        super.draw(batch, parentAlpha);
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof BufferWidget)) return false;
        if(this == obj) return true;
        if(this.getName().equalsIgnoreCase(((BufferWidget)obj).getName()))
            return true;
        return super.equals(obj);
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }
}