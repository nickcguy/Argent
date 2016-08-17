package net.ncguy.argent.editor.tools.picker;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import net.ncguy.argent.editor.project.EditorScene;
import net.ncguy.argent.editor.tools.ToolHandle;

/**
 * Created by Guy on 30/07/2016.
 */
public class ToolHandlePicker extends BasePicker<ToolHandle> {

    ToolHandle[] handles;

    public ToolHandlePicker() {
        super();
    }

    public ToolHandlePicker setHandles(ToolHandle[] handles) {
        this.handles = handles;
        return this;
    }

    @Override
    public ToolHandle pick(EditorScene scene, int screenX, int screenY) {
        Ray ray = scene.sceneGraph.renderer.camera().getPickRay(screenX, screenY);
        ToolHandle result = null;
        float distance = -1;
        Vector3 position = new Vector3();
        BoundingBox box = new BoundingBox();
        for (ToolHandle handle : handles) {

//            Mesh mesh = handle.getInstance().nodes.first().parts.first().meshPart.mesh;

//            short[] indices = new short[mesh.getNumIndices()];
//            mesh.getIndices(indices);
//            float[] vertices = new float[mesh.getNumVertices()];
//            mesh.getVertices(vertices);

            handle.getInstance().transform.getTranslation(position);
            handle.getInstance().calculateBoundingBox(box);
            Quaternion quat = handle.getInstance().transform.getRotation(new Quaternion());
            Vector3 min = box.getMin(new Vector3()).add(position).rotateRad(quat.w, quat.x, quat.y, quat.z);
            Vector3 max = box.getMax(new Vector3()).add(position).rotateRad(quat.w, quat.x, quat.y, quat.z);
            box.set(min, max);
            float dist2 = ray.origin.dst2(position);
            if(distance >= 0 && dist2 > distance) continue;
            if(Intersector.intersectRayBounds(ray, box, null)) {
                result = handle;
                distance = dist2;
            }
        }
        return result;
    }


    /*
    List<Vector3> trianglePoints = new ArrayList<>();
            Vector3 point = new Vector3();
            int i = 0;
            for(short index : indices) {
                switch(i) {
                    case 0: point.x = vertices[index]; break;
                    case 1: point.y = vertices[index]; break;
                    case 2: point.z = vertices[index]; break;
                }
                i++;
                if(i == 3) {
                    trianglePoints.addZone(point.cpy());
                    i = 0;
                }
            }

            while(trianglePoints.size() % 3 != 0) trianglePoints.addZone(new Vector3());
                        if(Intersector.intersectRayTriangles(ray, trianglePoints, null)) {

     */

}
