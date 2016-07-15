package net.ncguy.argent.render.sample.light.volumetric;

import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import net.ncguy.argent.render.WorldRenderer;

import java.util.List;

/**
 * Created by Guy on 12/07/2016.
 */
public class PointLight extends VolumetricLight {

    public PointLight(WorldRenderer world) {
        super(world);
    }

    public PointLight(WorldRenderer world, Quaternion lightData) {
        super(world, lightData);
    }

    public PointLight(WorldRenderer world, float x, float y, float z, float w) {
        super(world, x, y, z, w);
    }



    @Override
    public void applyToShader(ShaderProgram program) {
        final int texNum = GL30.GL_MAX_TEXTURE_UNITS-2;
    }

    // IRRELEVANT

    public void testHit(List<ModelInstance> instances, Ray ray) {
        instances.forEach(i -> testHit(i, ray));
    }

    public void testHit(ModelInstance i, Ray ray) {
        i.nodes.forEach(n -> testHit(n, ray));
    }

    public void testHit(Node n, Ray ray) {
        n.parts.forEach(p -> testHit(p, ray));
    }

    public void testHit(NodePart p, Ray ray) {
        testHit(p.meshPart.mesh, ray);
    }

    public void testHit(Mesh mesh, Ray ray) {
        int numVertices = mesh.getNumVertices();
        float[] vertices = new float[numVertices];
        mesh.getVertices(vertices);

        int numIndices = mesh.getNumIndices();
        short[] indices = new short[numIndices];
        mesh.getIndices(indices);

        Vector3 intersection = new Vector3();
        if (Intersector.intersectRayTriangles(ray, vertices, indices, mesh.getVertexSize(), intersection))
            hit(ray, intersection);
    }

    public void hit(Ray ray, Vector3 intersection) {
        System.out.println("Hit at "+intersection.toString());
    }
}
