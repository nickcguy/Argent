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
    public void highlight() {

    }

    public List<DropZone> getTargetZones() {
        return getTargetZones(this);
    }

    public void initDropZones() {
        List<DropZone> dropZones = getTargetZones();
        for (DropZone zone : dropZones) {
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
            target.getActor().setDebug(true);
            targets.add(target);

        }
    }

}
