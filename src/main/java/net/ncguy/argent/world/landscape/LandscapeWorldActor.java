package net.ncguy.argent.world.landscape;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import net.ncguy.argent.core.Meta;
import net.ncguy.argent.editor.ConfigurableAttribute;
import net.ncguy.argent.editor.IConfigurable;
import net.ncguy.argent.editor.shared.config.ConfigControl;
import net.ncguy.argent.utils.Reference;
import net.ncguy.argent.world.GameWorld;

import java.util.ArrayList;
import java.util.List;

import static com.badlogic.gdx.graphics.VertexAttributes.Usage.*;

/**
 * Created by Guy on 14/07/2016.
 */
public class LandscapeWorldActor implements IConfigurable {

    public LandscapeCell[][] cells;
    private ModelBuilder builder = new ModelBuilder();
    private Model model;
    private ModelInstance instance;
    protected float cellWidth = 100;
    protected float cellHeight = 100;

    protected float landscapeWidth;
    protected float landscapeHeight;

    protected Vector3 origin;
    private transient GameWorld.Generic world;

    public LandscapeWorldActor(GameWorld.Generic world, Vector3 origin, int width, int height, int cellWidth, int cellHeight) {
        this.world = world;
        this.cells = new LandscapeCell[width][height];
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        for(int x = 0; x < width; x++)
            for(int y = 0; y < height; y++)
                this.cells[x][y] = new LandscapeCell(this);

        this.landscapeWidth = cellWidth * width;
        this.landscapeHeight = cellHeight * height;
        this.origin = origin;
        constructCells();
    }

    public void setToTranslation(Vector3 trans) {
        instance().transform.setToTranslation(trans);
    }

    public ModelInstance instance() { return instance;}

    public void constructCells() {
        builder.begin();

        int vertAttrs = Position | Normal | TextureCoordinates;


        for(int x = 0; x < cells.length; x++) {
            for (int z = 0; z < cells[x].length; z++) {
                MeshPartBuilder partBuilder = builder.part("cell"+x+","+z, GL30.GL_TRIANGLES, vertAttrs, Reference.Defaults.Models.material());
                partBuilder.rect(getPoint("D", x, z), getPoint("C", x, z), getPoint("B", x, z), getPoint("A", x, z), Vector3.Y);
                cells[x][z].setCellPart(partBuilder.getMeshPart());
                cells[x][z].init();
            }
        }
        model = builder.end();
        instance = new ModelInstance(model);
    }

    protected Vector3 getCenterPoint(int x, int z) {
        Vector3 point = new Vector3();
        point.x = (cellWidth*x)+(cellWidth/2);
        point.y = 0;
        point.z = (cellHeight*z)+(cellHeight/2);

        point.x -= landscapeWidth/2;
        point.z -= landscapeHeight/2;
        return point;
    }

    protected Vector3 getPoint(String id, int x, int z) {
        id = id.toUpperCase();
        Vector3 info = new Vector3();
        switch(id) {
            case "A": info.set(getPointA(x, z));      break;
            case "B": info.set(getPointB(x, z));      break;
            case "C": info.set(getPointC(x, z));      break;
            case "D": info.set(getPointD(x, z));      break;
            default:  info.set(getCenterPoint(x, z)); break;
        }
        return info;
    }

    /**
     * AD<br>
     * BC
     */
    protected Vector3 getPointA(int x, int z) {
        Vector3 point = getCenterPoint(x, z);
        point.x -= cellWidth/2;
        point.z += cellHeight/2;
        return point;
    }
    protected Vector3 getPointB(int x, int z) {
        Vector3 point = getCenterPoint(x, z);
        point.x -= cellWidth/2;
        point.z -= cellHeight/2;
        return point;
    }
    protected Vector3 getPointC(int x, int z) {
        Vector3 point = getCenterPoint(x, z);
        point.x += cellWidth/2;
        point.z -= cellHeight/2;
        return point;
    }
    protected Vector3 getPointD(int x, int z) {
        Vector3 point = getCenterPoint(x, z);
        point.x += cellWidth/2;
        point.z += cellHeight/2;
        return point;
    }

    @Override
    public List<ConfigurableAttribute<?>> getConfigurableAttributes() {
        List<ConfigurableAttribute<?>> attrs = new ArrayList<>();
        attr(attrs, new Meta.Object("Origin X", "Landscape"), () -> origin.x, (val) -> { origin.x = val; setToTranslation(origin); }, ConfigControl.NUMBERSELECTOR, Float::parseFloat);
        attr(attrs, new Meta.Object("Origin Y", "Landscape"), () -> origin.y, (val) -> { origin.y = val; setToTranslation(origin); }, ConfigControl.NUMBERSELECTOR, Float::parseFloat);
        attr(attrs, new Meta.Object("Origin Z", "Landscape"), () -> origin.z, (val) -> { origin.z = val; setToTranslation(origin); }, ConfigControl.NUMBERSELECTOR, Float::parseFloat);
        return attrs;
    }

    private List<LandscapeCell> packedCells;
    public List<LandscapeCell> packedCells() {
        if(packedCells == null) {
            packedCells = new ArrayList<>();
            for(int x = 0; x < cells.length; x++) {
                for(int y = 0; y < cells[x].length; y++) {
                    packedCells.add(cells[x][y]);
                }
            }
        }
        return packedCells;
    }
    public void invalidatePackedCells() {
        packedCells = null;
    }

    public LandscapeCell rayIntersects() {
        if(true) return null;
        Ray ray = new Ray();
        Camera cam = world.renderer().camera();
        ray.origin.set(cam.position);
        ray.direction.set(cam.direction);
        return rayIntesects(ray, null);
    }

    public LandscapeCell rayIntesects(Ray ray, Vector3 intersection) {
        List<Vector3> triangleList = new ArrayList<>();
        packedCells().forEach(c -> triangleList.addAll(c.trianglePoints()));
        if(Intersector.intersectRayTriangles(ray, triangleList, intersection)) {
            for (LandscapeCell cell : packedCells) {
                if(cell.rayIntersects(ray)) return cell;
            }
        }
        return null;
    }
}
