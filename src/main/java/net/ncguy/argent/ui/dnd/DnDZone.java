package net.ncguy.argent.ui.dnd;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.kotcrab.vis.ui.VisUI;
import net.ncguy.argent.Argent;
import net.ncguy.argent.injector.ArgentInjector;
import net.ncguy.argent.injector.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Guy on 01/08/2016.
 */
public abstract class DnDZone extends Table {

    public static List<DragZone> dragZones = new ArrayList<>();
    public static List<DropZone> dropZones = new ArrayList<>();
    public static List<DragDropZone> hybridZones = new ArrayList<>();


    private static ShapeRenderer renderer;
    protected static ShapeRenderer renderer() {
        if(renderer == null)
            renderer = new ShapeRenderer();
        return renderer;
    }


    public boolean hasTag(DnDZone zone) {
        return hasTag(zone.getTag());
    }
    /**
     * Used for dropzone recognition
     * @return
     */
    public abstract boolean hasTag(String tag);
    public abstract String getTag();

    static {
        Argent.addOnResize(DnDZone::invalidateRenderer);
    }

    private static void invalidateRenderer(int w, int h) {
        invalidateRenderer();
    }
    protected static void invalidateRenderer() {
        if(renderer != null) {
            renderer.dispose();
            renderer = null;
        }
    }

    protected static void addZone(DnDZone zone) {
        if(zone instanceof DragDropZone)
            hybridZones.add((DragDropZone) zone);
        else if(zone instanceof DragZone)
            dragZones.add((DragZone) zone);
        else if(zone instanceof DropZone)
            dropZones.add((DropZone) zone);
    }

    public static List<DnDZone> getTargetZones(final DnDZone source) {
        final List<DnDZone> zones = new ArrayList<>();
        zones.addAll(dropZones.stream().filter(z -> z.hasTag(source.getTag())).collect(Collectors.toList()));
        zones.addAll(hybridZones.stream().filter(z -> z.hasTag(source.getTag())).collect(Collectors.toList()));
        zones.remove(source); // Just in case, should only happen with hybrid zones
        return zones;
    }

    @Inject
    protected DragAndDrop dnd;

    public DragAndDrop dnd() { return dnd; }

    public DnDZone() {
        super(VisUI.getSkin());
        ArgentInjector.inject(this);
        addZone(this);
    }

    public abstract void highlight();

    public abstract void onDrop_Safe(Object object);
    public abstract void onHover(TargetDragPayload dragPayload);
    public abstract void onReset(TargetResetPayload resetPayload);

    public static class TargetDragPayload {
        public DragAndDrop.Source src;
        public DragAndDrop.Payload payload;
        public float x;
        public float y;
        public int pointer;

        public TargetDragPayload(DragAndDrop.Source src, DragAndDrop.Payload payload, float x, float y, int pointer) {
            this.src = src;
            this.payload = payload;
            this.x = x;
            this.y = y;
            this.pointer = pointer;
        }
    }
    public static class TargetResetPayload {
        public DragAndDrop.Source src;
        public DragAndDrop.Payload payload;

        public TargetResetPayload(DragAndDrop.Source src, DragAndDrop.Payload payload) {
            this.src = src;
            this.payload = payload;
        }
    }


}

