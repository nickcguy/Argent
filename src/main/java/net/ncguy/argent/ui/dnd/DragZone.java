package net.ncguy.argent.ui.dnd;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guy on 01/08/2016.
 */
public class DragZone extends DnDZone {

    String tag;
    public List<DragAndDrop.Target> targets = new ArrayList<>();

    public DragZone(String tag) {
        super();
        this.tag = tag;
    }

    public boolean hasTag(DropZone dropZone) {
        return dropZone.hasTag(tag);
    }

    @Override
    public boolean hasTag(String tag) {
        return this.tag.equalsIgnoreCase(tag);
    }

    @Override public String getTag() { return tag; }

    @Override public void onDrop_Safe(Object object) {}
    @Override public void onHover(TargetDragPayload dragPayload) {}
    @Override public void onReset(TargetResetPayload resetPayload) {}

    public List<DnDZone> getTargetZones() {
        return getTargetZones(this);
    }

    public void initDropZones() {
        List<DnDZone> dropZones = getTargetZones();
        for (DnDZone zone : dropZones) {
            DragAndDrop.Target target;
            dnd.addTarget(target = new DragAndDrop.Target(zone) {
                @Override
                public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                    getActor().setColor(Color.GREEN);
                    return true;
                }

                @Override
                public void reset(DragAndDrop.Source source, DragAndDrop.Payload payload) {
                    getActor().setColor(Color.WHITE);
                }

                @Override
                public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                    zone.onDrop_Safe(payload.getObject());
                }
            });
            zone.highlight(true);
            targets.add(target);

        }
    }

}
