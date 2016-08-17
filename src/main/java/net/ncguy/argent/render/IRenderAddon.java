package net.ncguy.argent.render;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

/**
 * Created by Guy on 16/07/2016.
 */
public interface IRenderAddon {

    default void init(Camera camera) {};
    void render(ModelBatch rootBatch, float delta);

}
