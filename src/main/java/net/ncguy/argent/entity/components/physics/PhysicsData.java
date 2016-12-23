package net.ncguy.argent.entity.components.physics;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.VisUI;

import java.util.List;

/**
 * Created by Guy on 22/09/2016.
 */
public abstract class PhysicsData {

    protected PhysicsComponent parentComponent;

    public abstract String name();
    public abstract void generateBoundingBox(BoundingBox box);

    public PhysicsData(PhysicsComponent parentComponent) {
        this.parentComponent = parentComponent;
    }

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

    public void toTris(List<Float> floats) {

        BoundingBox box = generateBoundingBox();

        Vector3 p000 = box.getCorner000(new Vector3());
        Vector3 p001 = box.getCorner001(new Vector3());
        Vector3 p010 = box.getCorner010(new Vector3());
        Vector3 p011 = box.getCorner011(new Vector3());
        Vector3 p100 = box.getCorner100(new Vector3());
        Vector3 p101 = box.getCorner101(new Vector3());
        Vector3 p110 = box.getCorner110(new Vector3());
        Vector3 p111 = box.getCorner111(new Vector3());

        faceToTris(floats, p000, p100, p010, p110);
        faceToTris(floats, p001, p000, p011, p010);
        faceToTris(floats, p100, p101, p110, p111);
        faceToTris(floats, p010, p110, p011, p111);
        faceToTris(floats, p101, p001, p111, p011);
        faceToTris(floats, p101, p001, p100, p000);

    }

    public void faceToTris(List<Float> floats, Vector3 face00, Vector3 face01,  Vector3 face10, Vector3 face11) {
        // Tri 0
        floats.add(face00.x);
        floats.add(face00.y);
        floats.add(face00.z);
        floats.add(face01.x);
        floats.add(face01.y);
        floats.add(face01.z);
        floats.add(face10.x);
        floats.add(face10.y);
        floats.add(face10.z);

        // Tri 1
        floats.add(face11.x);
        floats.add(face11.y);
        floats.add(face11.z);
        floats.add(face01.x);
        floats.add(face01.y);
        floats.add(face01.z);
        floats.add(face10.x);
        floats.add(face10.y);
        floats.add(face10.z);
    }

}
