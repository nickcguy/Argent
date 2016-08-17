package net.ncguy.argent.ui.dnd;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import net.ncguy.argent.ui.patch.tiled.TiledBorder;

import java.util.function.Consumer;

/**
 * Created by Guy on 01/08/2016.
 */
public class DropZone<T> extends DnDZone {

    protected Class<T> cls;
    protected String[] tags;
    protected TiledBorder border = new TiledBorder("Texture_TiledBorder");
    protected Consumer<T> onDrop;

    public DropZone(Class<T> cls, String... tags) {
        super();
        this.cls = cls;
        this.tags = tags;
        setTouchable(Touchable.enabled);
    }

    public boolean hasTag(String tag) {
        for (String s : this.tags)
            if(s.equalsIgnoreCase(tag)) return true;
        return false;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        border.draw(batch, getX(), getY(), getWidth(), getHeight());
    }

    public void onDrop_Safe(Object obj) {
        if(cls().isAssignableFrom(obj.getClass()))
            onDrop((T) obj);
    }

    public void onDrop(T obj) {
        if(onDrop != null) onDrop.accept(obj);
    }

    public Consumer<T> getOnDrop() { return onDrop; }
    public void setOnDrop(Consumer<T> onDrop) { this.onDrop = onDrop; }

    @Override
    public void highlight() {

    }

    public Class<T> cls() {
        return cls;
    }

}
