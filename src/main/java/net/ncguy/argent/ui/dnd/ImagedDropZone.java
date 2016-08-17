package net.ncguy.argent.ui.dnd;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * Created by Guy on 05/08/2016.
 */
public class ImagedDropZone<T> extends DropZone<T> {

    Drawable image;

    public ImagedDropZone(Class<T> cls, String... tags) {
        super(cls, tags);
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if(this.image != null) this.image.draw(batch, getX(), getY(), getWidth(), getHeight());
    }
}
