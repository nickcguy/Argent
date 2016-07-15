package net.ncguy.argent.world.landscape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import net.ncguy.argent.Argent;
import net.ncguy.argent.core.Meta;
import net.ncguy.argent.editor.ConfigurableAttribute;
import net.ncguy.argent.editor.IConfigurable;
import net.ncguy.argent.editor.shared.config.ConfigControl;
import net.ncguy.argent.utils.MathsUtils;
import net.ncguy.argent.utils.Reference;

import java.util.ArrayList;
import java.util.List;

import static com.badlogic.gdx.graphics.VertexAttributes.Usage.*;

/**
 * Created by Guy on 14/07/2016.
 */
public class Terrain implements RenderableProvider, Disposable, IConfigurable {

    private static final MeshPartBuilder.VertexInfo tempVertexInfo = new MeshPartBuilder.VertexInfo();
    private static final Vector3 c00 = new Vector3();
    private static final Vector3 c01 = new Vector3();
    private static final Vector3 c10 = new Vector3();
    private static final Vector3 c11 = new Vector3();

    public long id;
    private VertexAttributes attribs;
    private final Vector2 uvScale = new Vector2(60, 60);
    private float vertices[];
    private int stride;
    private int posPos;
    private int norPos;
    private int uvPos;

    public Matrix4 transform;
    public float[] heightData;
    public int terrainWidth = 1200;
    public int terrainDepth = 1200;
    public int vertexResolution;

    public String name;
    public String terraPath;

    public Material material;

    private Model model;
    public ModelInstance modelInstance;
    private Mesh mesh;

    public Terrain(int vertexResolution) {
        this.transform = new Matrix4();
        this.attribs = MeshBuilder.createAttributes(Position | Normal | TextureCoordinates);
        this.posPos = attribs.getOffset(Position, -1);
        this.norPos = attribs.getOffset(Normal, -1);
        this.uvPos = attribs.getOffset(TextureCoordinates, -1);
        this.stride = attribs.vertexSize / 4;

        this.vertexResolution = vertexResolution;
        this.heightData = new float[vertexResolution * vertexResolution];

        material = Reference.Defaults.Models.material();
    }

    public Terrain(int vertexResolution, int width, int depth, float[] heightData) {
        this(vertexResolution);
        this.terrainWidth = width;
        this.terrainDepth = depth;
        this.heightData = heightData;
    }

    public void transform(Matrix4 transform) {
        this.transform = transform;
        modelInstance.transform = this.transform;
    }

    public void init() {
        final int numVertices = this.vertexResolution * this.vertexResolution;
        final int numIndices = (this.vertexResolution-1) * (this.vertexResolution-1) * 6;

        mesh = new Mesh(true, numVertices, numIndices, attribs);
        this.vertices = new float[numVertices * stride];
        mesh.setIndices(buildIndices());
        buildVertices();
        mesh.setVertices(vertices);

        MeshPart meshPart = new MeshPart(null, mesh, 0, numIndices, GL30.GL_TRIANGLES);
        meshPart.update();
        ModelBuilder mb = new ModelBuilder();
        mb.begin();
        mb.part(meshPart, material);
        model = mb.end();
        modelInstance = new ModelInstance(model);
        modelInstance.transform = transform;
    }

    public Vector3 getVertexPosition(Vector3 out, int x, int z) {
        final float dx = (float)x / (float)(vertexResolution-1);
        final float dz = (float)z / (float)(vertexResolution-1);
        final float height = heightData[z * vertexResolution + x];
        out.set(dx * this.terrainWidth, height, dz * this.terrainDepth);
        return out;
    }

    public void setVertexHeight(float height, int x, int z) {
        int heightIndex = z * vertexResolution + x;
        if(heightData.length <= heightIndex) {
            Argent.toast("Terrain", "No height data at index "+heightIndex);
            return;
        }
        heightData[heightIndex] = height;
    }

    public float getHeightAtWorldCoord(float worldX, float worldZ) {
        transform.getTranslation(c00);
        float terrainX = worldX - c00.x;
        float terrainZ = worldZ - c00.z;

        float gridSquareSize = terrainWidth / ((float)vertexResolution - 1);
        int gridX = (int)Math.floor(terrainX / gridSquareSize);
        int gridZ = (int)Math.floor(terrainZ / gridSquareSize);

        if(gridX >= vertexResolution - 1 || gridZ >= vertexResolution - 1 || gridX < 0 || gridZ < 0) return 0;

        float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
        float zCoord = (terrainZ % gridSquareSize) / gridSquareSize;

        c01.set(1, heightData[(gridZ+1) * vertexResolution + gridX], 0);
        c10.set(0, heightData[gridZ * vertexResolution + gridX+1], 1);

        if(xCoord <= (1 - zCoord)) {
            c00.set(0, heightData[gridZ * vertexResolution + gridX], 0);
            return MathsUtils.barryCentric(c00, c10, c01, new Vector2(zCoord, xCoord));
        }
        c11.set(1, heightData[(gridZ+1) * vertexResolution + gridX+1], 1);
        return MathsUtils.barryCentric(c10, c11, c01, new Vector2(zCoord, xCoord));
    }

    public Vector3 getRayIntersection(Vector3 out, Ray ray) {
        float curDistance = 0;
        int rounds = 0;
        ray.getEndPoint(out, curDistance);
        boolean isUnder = isUnderTerrain(out);
        while(true) {
            rounds++;
            ray.getEndPoint(out, curDistance);
            boolean u = isUnderTerrain(out);
            if(u != isUnder || rounds >= 10000)
                return out;
            curDistance += u ? -.1f : .1f;
        }
    }

    private short[] buildIndices() {
        final int w = vertexResolution - 1;
        final int h = vertexResolution - 1;
        short indices[] = new short[w * h * 6];
        int i = -1;
        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {
                final int c00 = y * vertexResolution + x;
                final int c10 = c00 + 1;
                final int c01 = c00 + vertexResolution;
                final int c11 = c10 + vertexResolution;
                indices[++i] = (short)c11;
                indices[++i] = (short)c10;
                indices[++i] = (short)c00;
                indices[++i] = (short)c00;
                indices[++i] = (short)c01;
                indices[++i] = (short)c11;
            }
        }
        return indices;
    }

    private void buildVertices() {
        for(int x = 0; x < vertexResolution; x++) {
            for(int z = 0; z < vertexResolution; z++) {
                calculateVertexAt(tempVertexInfo, x, z);
                calculateNormalAt(tempVertexInfo, x, z);
                setVertex(z * vertexResolution + x, tempVertexInfo);
            }
        }
    }

    private void setVertex(int index, MeshPartBuilder.VertexInfo info) {
        index *= stride;
        if(posPos >= 0) {
            vertices[index + posPos + 0] = info.position.x;
            vertices[index + posPos + 1] = info.position.y;
            vertices[index + posPos + 2] = info.position.z;
        }
        if(uvPos >= 0) {
            vertices[index + uvPos + 0] = info.uv.x;
            vertices[index + uvPos + 1] = info.uv.y;
        }
        if(norPos >= 0) {
            vertices[index + norPos + 0] = info.normal.x;
            vertices[index + norPos + 1] = info.normal.y;
            vertices[index + norPos + 2] = info.normal.z;
        }
    }

    private MeshPartBuilder.VertexInfo calculateVertexAt(MeshPartBuilder.VertexInfo out, int x, int z) {
        final float dx = (float)x / (float)(vertexResolution - 1);
        final float dz = (float)z / (float)(vertexResolution - 1);
        final float height = heightData[z * vertexResolution + x];

        out.position.set(dx * this.terrainWidth, height, dz * this.terrainDepth);
        out.uv.set(dx, dz).scl(uvScale);

        return out;
    }

    private MeshPartBuilder.VertexInfo calculateNormalAt(MeshPartBuilder.VertexInfo out, int x, int z) {
        // handle edges of terrain
        int xP1 = (x+1 >= vertexResolution) ? vertexResolution -1 : x+1;
        int yP1 = (z+1 >= vertexResolution) ? vertexResolution -1 : z+1;
        int xM1 = (x-1 < 0) ? 0 : x-1;
        int yM1 = (z-1 < 0) ? 0 : z-1;

        float hL = heightData[z * vertexResolution + xM1];
        float hR = heightData[z * vertexResolution + xP1];
        float hD = heightData[yM1 * vertexResolution + x];
        float hU = heightData[yP1 * vertexResolution + x];
        out.normal.x = hL - hR;
        out.normal.y = 2;
        out.normal.z = hD - hU;
        out.normal.nor();

        return out;
    }

    public boolean isUnderTerrain(Vector3 worldCoords) {
        float terrainHeight = getHeightAtWorldCoord(worldCoords.x, worldCoords.z);
        return terrainHeight > worldCoords.y;
    }

    public boolean isOnTerrain(float worldX, float worldZ) {
        transform.getTranslation(c00);
        return worldX >= c00.x && worldX <= c00.x + terrainWidth && worldZ >= c00.z && worldZ <= c00.z + terrainDepth;
    }

    public Vector3 getPosition(Vector3 out) {
        transform.getTranslation(out);
        return out;
    }

    public void update() {
        buildVertices();
        mesh.setVertices(vertices);
    }

    @Override
    public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
            heightData[MathUtils.random(0, heightData.length-1)] = MathUtils.random(0, 1024);
            update();
        }
        modelInstance.nodes.forEach(n -> n.parts.forEach(p -> p.meshPart.primitiveType = GL30.GL_LINES));
        modelInstance.getRenderables(renderables, pool);
    }

    @Override
    public void dispose() {

    }


    public int targetX = 0;
    public float targetHeight = 0;
    public int targetZ = 0;
    public boolean apply = false;

    private void applyChanges(Object noop) {
        setVertexHeight(targetHeight, targetX, targetZ);
        update();
    }

    @Override
    public List<ConfigurableAttribute<?>> getConfigurableAttributes() {
        List<ConfigurableAttribute<?>> attrs = new ArrayList<>();

        attr(attrs, new Meta.Object("Target X", "Terrain Height"), () -> targetX, (val) -> targetX = val, ConfigControl.NUMBERSELECTOR, (s) -> { s = s.substring(0, s.indexOf('.')); return Integer.parseInt(s); });
        attr(attrs, new Meta.Object("Target Z", "Terrain Height"), () -> targetZ, (val) -> targetZ = val, ConfigControl.NUMBERSELECTOR, (s) -> { s = s.substring(0, s.indexOf('.')); return Integer.parseInt(s); });
        ConfigurableAttribute attr = attr(attrs, new Meta.Object("Height", "Terrain Height"), () -> targetHeight, (val) -> targetHeight = val, ConfigControl.NUMBERSELECTOR, Float::parseFloat);
        attr.addParam("min", Integer.class, -256);
        attr.addParam("max", Integer.class, 256);
        attr(attrs, new Meta.Object("Apply changes", "Terrain Height"), () -> apply, this::applyChanges, ConfigControl.CHECKBOX, Boolean::getBoolean);

        return attrs;
    }
}
