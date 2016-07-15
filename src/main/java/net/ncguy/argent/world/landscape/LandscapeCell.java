package net.ncguy.argent.world.landscape;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import net.ncguy.argent.Argent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guy on 14/07/2016.
 */
public class LandscapeCell {

    private transient Texture texture;
    public String textureRef;
    public Mesh cellMesh;
    public MeshPart cellNode;
    protected LandscapeWorldActor landscape;

    public LandscapeCell(LandscapeWorldActor landscape) {
        this.landscape = landscape;
    }

    public List<Vector3> trianglePoints() {
        List<Vector3> tris = new ArrayList<>();
        try {
            int offset = offset();
            int triCount = 3;
            int pointCount = 9;
            int vertCount = triCount * pointCount;
            float[] vertices = new float[vertCount];
            if (isMeshIndexed()) {
                short[] indices = new short[vertCount];
                cellMesh.getIndices(offset, vertCount, indices, 0);
                float[] allVerts = new float[cellMesh.getNumVertices()];
                cellMesh.getVertices(allVerts);
                for (int i = 0; i < vertCount; i++) {
                    short index = indices[i];
                    vertices[i] = allVerts[index];
                }
            } else {
                cellMesh.getVertices(offset, vertices);
            }
            for (int i = 0; i < vertices.length; i++)
                tris.add(new Vector3(vertices[i], vertices[i++], vertices[i++]));
        }catch (Exception e) {
            e.printStackTrace();
        }
        return tris;
    }

    public int offset() {
        int offset = Short.MAX_VALUE;
        if(cellNode.offset < offset) offset = cellNode.offset;
        return offset;
    }

    public Mesh cellMesh() {
        if(cellMesh == null) {
            setCellMesh(cellNode.mesh);

        }
        return cellMesh;
    }

    public boolean isMeshIndexed() {
        return cellMesh().getNumIndices() > 0;
    }

    public boolean rayIntersects(Ray ray) {
        return Intersector.intersectRayTriangles(ray, trianglePoints(), null);
    }

    protected void init() {
        int seed = MathUtils.random(0, 64);
        if(seed < 12) {
//                Attribute attr = cellNode.material.get(ColorAttribute.Diffuse);
//                if(attr == null) cellNode.material.set(attr = ColorAttribute.createDiffuse(Color.WHITE));
//                ((ColorAttribute)attr).color.set(MathsUtils.randomColour());
            if(seed < 5) {
                cellNode.center.y = MathUtils.random(-128, 128);
            }
        }
    }

    public void setCellPart(MeshPart part) { this.cellNode = part; }
    public void setCellMesh(Mesh cellMesh) { this.cellMesh = cellMesh; }

    public void setTexture(String ref) {
        Texture tex = Argent.content.get(ref, Texture.class);
        if(tex != null) {
            this.textureRef = ref;
            this.texture = tex;
        }
    }

}
