package net.ncguy.argent.editor.wrapper;

import com.badlogic.gdx.graphics.g3d.ModelInstance;

/**
 * Created by Guy on 09/07/2016.
 */
public class ModelInstanceWrapper {

    public transient ModelInstance instance;

    public ModelInstanceWrapper(ModelInstance instance) {
        this.instance = instance;
    }
}
