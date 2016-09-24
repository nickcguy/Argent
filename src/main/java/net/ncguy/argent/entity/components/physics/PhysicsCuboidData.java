package net.ncguy.argent.entity.components.physics;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

/**
 * Created by Guy on 22/09/2016.
 */
public class PhysicsCuboidData extends PhysicsData {

    public float halfWidth, halfHeight, halfDepth;

    public PhysicsCuboidData(float halfWidth, float halfHeight, float halfDepth) {
        this.halfWidth = halfWidth;
        this.halfHeight = halfHeight;
        this.halfDepth = halfDepth;
    }

    public PhysicsCuboidData() {
        this(.5f, .5f, .5f);
    }

    public float getHalfWidth()  { return halfWidth;  }
    public float getHalfHeight() { return halfHeight; }
    public float getHalfDepth()  { return halfDepth;  }

    public void setHalfWidth(float halfWidth)   { this.halfWidth  = Math.abs(halfWidth);  }
    public void setHalfHeight(float halfHeight) { this.halfHeight = Math.abs(halfHeight); }
    public void setHalfDepth(float halfDepth)   { this.halfDepth  = Math.abs(halfDepth);  }

    @Override
    public String name() {
        return "Cuboid";
    }

    @Override
    public void generateBoundingBox(BoundingBox box) {
        halfWidth = Math.abs(halfWidth);
        halfHeight = Math.abs(halfHeight);
        halfDepth = Math.abs(halfDepth);

        box.set(new Vector3(-halfWidth, -halfHeight, -halfDepth), new Vector3(halfWidth, halfHeight, halfDepth));
    }
}
