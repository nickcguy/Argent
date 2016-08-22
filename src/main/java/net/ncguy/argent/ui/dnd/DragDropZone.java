package net.ncguy.argent.ui.dnd;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Guy on 20/08/2016.
 */
public class DragDropZone<T> extends DnDZone {

    protected String tag; // Drag tag
    protected String[] tags; // Drop Tags
    protected Class<T> cls;
    protected Consumer<T> onDrop;
    protected Consumer<TargetDragPayload> onHover;
    protected Consumer<TargetResetPayload> onReset;

    public DragDropZone(String tag, Class<T> cls, String... tags) {
        this.tag = tag;
        this.tags = tags;
        this.cls = cls;
        setTouchable(Touchable.enabled);
    }

    @Override
    public boolean hasTag(String tag) {
        for (String s : this.tags)
            if(s.equalsIgnoreCase(tag)) return true;
        return false;
    }

    @Override
    public String getTag() {
        return tag;
    }

    @Override
    public void highlight() {

    }

    @Override
    public void onDrop_Safe(Object obj) {
        if(cls().isAssignableFrom(obj.getClass()))
            onDrop((T) obj);
    }

    public void onDrop(T obj) {
        if(onDrop != null) onDrop.accept(obj);
    }

    public Class<T> cls() { return cls; }
    public Consumer<T> getOnDrop() { return onDrop; }
    public void setOnDrop(Consumer<T> onDrop) { this.onDrop = onDrop; }

    public List<DnDZone> getTargetZones() {
        return getTargetZones(this);
    }
    public List<DragAndDrop.Target> targets = new ArrayList<>();

    public void initDropZones() {
        List<DnDZone> dropZones = getTargetZones();
        for (DnDZone zone : dropZones) {
            DragAndDrop.Target target;
            dnd.addTarget(target = new DragAndDrop.Target(zone) {
                @Override
                public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                    getActor().setColor(Color.GREEN);
                    TargetDragPayload dragPayload = new TargetDragPayload(source, payload, x, y, pointer);
                    zone.onHover(dragPayload);
                    return true;
                }

                @Override
                public void reset(DragAndDrop.Source source, DragAndDrop.Payload payload) {
                    getActor().setColor(Color.WHITE);
                    TargetResetPayload resetPayload = new TargetResetPayload(source, payload);
                    zone.onReset(resetPayload);
                }

                @Override
                public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                    zone.onDrop_Safe(payload.getObject());
                }
            });
            target.getActor().setDebug(true);
            targets.add(target);
        }
    }

    public Consumer<DragDropZone.TargetDragPayload> getOnHover() { return onHover; }
    public void setOnHover(Consumer<DragDropZone.TargetDragPayload> onHover) { this.onHover = onHover; }
    public Consumer<DragDropZone.TargetResetPayload> getOnReset() { return onReset; }
    public void setOnReset(Consumer<DragDropZone.TargetResetPayload> onReset) { this.onReset = onReset; }

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

}
