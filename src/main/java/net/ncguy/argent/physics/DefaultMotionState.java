package net.ncguy.argent.physics;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;

/**
 * Created by Guy on 21/06/2016.
 */
public class DefaultMotionState extends btDefaultMotionState {

    public Matrix4 transform;

    @Override
    public void getWorldTransform(Matrix4 worldTrans) {
        worldTrans.set(transform);
    }

    @Override
    public void setWorldTransform(Matrix4 worldTrans) {
        transform.set(worldTrans);
    }
}
