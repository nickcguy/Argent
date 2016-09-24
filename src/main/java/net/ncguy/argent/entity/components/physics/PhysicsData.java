package net.ncguy.argent.entity.components.physics;

import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.VisUI;

/**
 * Created by Guy on 22/09/2016.
 */
public abstract class PhysicsData {

    public abstract String name();
    public abstract void generateBoundingBox(BoundingBox box);

    public BoundingBox generateBoundingBox() {
        BoundingBox box = new BoundingBox();
        generateBoundingBox(box);
        return box;
    }

    public Table generateConfigTable() {
        Table table = new Table(VisUI.getSkin());
        generateConfigTable(table);
        return table;
    }

    public void generateConfigTable(Table table) {
        table.add("No table definition found for "+name()).grow().row();
    }

}
