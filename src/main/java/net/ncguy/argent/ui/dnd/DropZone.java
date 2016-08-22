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
    protected Consumer<TargetDragPayload> onHover;
    protected Consumer<TargetResetPayload> onReset;

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
    public String getTag() {
        return null;
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        border.draw(batch, getX(), getY(), getWidth(), getHeight());
    }

    @Override
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

    public Consumer<TargetDragPayload> getOnHover() { return onHover; }
    public void setOnHover(Consumer<TargetDragPayload> onHover) { this.onHover = onHover; }
    public Consumer<TargetResetPayload> getOnReset() { return onReset; }
    public void setOnReset(Consumer<TargetResetPayload> onReset) { this.onReset = onReset; }

    @Override
    public void onHover(DragDropZone.TargetDragPayload t) {
        if(onHover != null)
            onHover.accept(t);
    }

    @Override
    public void onReset(DragDropZone.TargetResetPayload t) {
        if(onReset != null)
            onReset.accept(t);
    }

    public Class<T> cls() {
        return cls;
    }

}
