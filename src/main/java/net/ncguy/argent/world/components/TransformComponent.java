package net.ncguy.argent.world.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Pool;

/**
 * Created by Guy on 21/06/2016.
 */
public class TransformComponent implements Component, Pool.Poolable {

    public Matrix4 transform;

    public TransformComponent() {
        this(new Matrix4());
    }

    public TransformComponent(Matrix4 transform) {
        this.transform = transform;
    }

    @Override
    public void reset() {
        this.transform.setToScaling(1, 1, 1);
        this.transform.setToTranslation(0, 0, 0);
        this.transform.setFromEulerAngles(0, 0, 0);
    }
}
