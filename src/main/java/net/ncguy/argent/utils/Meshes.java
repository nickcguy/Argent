package net.ncguy.argent.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

import static com.badlogic.gdx.graphics.GL20.GL_LINES;
import static com.badlogic.gdx.graphics.GL20.GL_TRIANGLES;
import static com.badlogic.gdx.graphics.VertexAttributes.Usage.ColorUnpacked;
import static com.badlogic.gdx.graphics.VertexAttributes.Usage.Position;

/**
 * Created by Guy on 27/07/2016.
 */
public class Meshes {

    private static final VertexInfo v0 = new VertexInfo();
    private static final VertexInfo v1 = new VertexInfo();

    public static Model axes() {
        final float GRID_MIN  = -10;
        final float GRID_MAX  =  10;
        final float GRID_STEP =   1;
        ModelBuilder mb = new ModelBuilder();
        mb.begin();
        MeshPartBuilder builder = mb.part("grid", GL_LINES, Position | ColorUnpacked, new Material());
        builder.setColor(Color.LIGHT_GRAY);
        for(float t = GRID_MIN; t <= GRID_MAX; t += GRID_STEP) {
            builder.line(t, 0, GRID_MIN, t, 0, GRID_MAX);
            builder.line(GRID_MIN, 0, t, GRID_MAX, 0, t);
        }
        builder = mb.part("axes", GL_LINES, Position | ColorUnpacked, new Material());
        builder.setColor(Color.RED);
        builder.line(0, 0, 0, 100, 0, 0);
        builder.setColor(Color.GREEN);
        builder.line(0, 0, 0, 0, 100, 0);
        builder.setColor(Color.BLUE);
        builder.line(0, 0, 0, 0, 0, 100);
        return mb.end();
    }

    public static Model torus(Material mat, float width, float height, int divisionsU, int divisionsV) {
        ModelBuilder mb = new ModelBuilder();
        mb.begin();
        MeshPartBuilder builder = mb.part("torus", GL_TRIANGLES, Position, mat);

        VertexInfo curr1 = v0.set(null, null, null, null);
        curr1.hasUV = curr1.hasNormal = false;
        curr1.hasPosition = true;

        VertexInfo curr2 = v1.set(null, null, null, null);
        curr2.hasUV = curr2.hasNormal = false;
        curr2.hasPosition = true;

        short i1, i2, i3 = 0, i4 = 0;
        double s, t, tau;
        tau = 2 * Math.PI;

        for(int i = 0; i < divisionsV; i++) {
            for(int j = 0; j <= divisionsU; j++) {
                int k = 1;
//                for(int k = 1; k >= 0; k--) {
                    s = (i + k) % divisionsV + .5;
                    t = j % divisionsU;
                    curr1.position.set(
                            (float)((width + height * Math.cos(s * tau / divisionsV)) * Math.cos(t * tau / divisionsU)),
                            (float)((width + height * Math.cos(s * tau / divisionsV)) * Math.sin(t * tau / divisionsU)),
                            (float)(height * Math.sin(s * tau / divisionsV))
                    );
                    k--;
                s = (i + k) % divisionsV + .5;
                curr2.position.set(
                        (float) ((width + height * Math.cos(s * tau / divisionsV)) * Math.cos(t * tau / divisionsU)),
                        (float) ((width + height * Math.cos(s * tau / divisionsV)) * Math.sin(t * tau / divisionsU)),
                        (float) (height * Math.sin(s * tau / divisionsV)));
//                }
                i1 = builder.vertex(curr1);
                i2 = builder.vertex(curr2);
                builder.rect(i4, i2, i1, i3);
                i3 = i1;
                i4 = i2;
            }
        }
        return mb.end();
    }

}
