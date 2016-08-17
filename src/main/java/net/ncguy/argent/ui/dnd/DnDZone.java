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

    private static ShapeRenderer renderer;
    protected static ShapeRenderer renderer() {
        if(renderer == null)
            renderer = new ShapeRenderer();
        return renderer;
    }

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
        if(zone instanceof DragZone)
            dragZones.add((DragZone) zone);
        else if(zone instanceof DropZone)
            dropZones.add((DropZone) zone);
    }

    public static List<DropZone> getTargetZones(DragZone source) {
        return dropZones.stream().filter(source::hasTag).collect(Collectors.toList());
    }

    @Inject
    protected DragAndDrop dnd;

    public DnDZone() {
        super(VisUI.getSkin());
        ArgentInjector.inject(this);
        addZone(this);
    }

    public abstract void highlight();
}

