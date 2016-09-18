package net.ncguy.argent.editor.tools;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import net.ncguy.argent.entity.attributes.PickerIDAttribute;
import net.ncguy.argent.misc.shader.Shaders;

/**
 * Created by Guy on 30/07/2016.
 */
public abstract class ToolHandle implements Disposable, RenderableProvider {

    public final int id;

    public final Vector3 position;
    public final Vector3 rotationEuler;
    public final Quaternion rotation;
    public final Vector3 scale;
    public final PickerIDAttribute idAttribute;

    protected Model model;
    protected ModelInstance instance;

    public ToolHandle(Color id, Model model) {
        this(id.toIntBits(), model);
    }

    public ToolHandle(int id, Model model) {
        this.id = id;
        this.position = new Vector3();
        this.rotationEuler = new Vector3();
        this.rotation = new Quaternion();
        this.scale = new Vector3();
        this.idAttribute = new PickerIDAttribute();
        PickerIDAttribute.encodeID(id, this.idAttribute);

        this.model = model;
        this.instance = new ModelInstance(model);
        this.instance.materials.first().set(idAttribute);
    }

    public void setColour(Color colour) {
        ColorAttribute diffuse;
        if(instance.materials.first().has(ColorAttribute.Diffuse)) {
            diffuse = (ColorAttribute) instance.materials.get(0).get(ColorAttribute.Diffuse);
            diffuse.color.set(colour);
        }else{
            diffuse = ColorAttribute.createDiffuse(colour);
            instance.materials.first().set(diffuse);
        }
    }

    public abstract void init();

    public void render(ModelBatch batch) {
//        batch.render(instance, Shaders.instance().maskedShader);
        batch.render(instance, Shaders.instance().solidShader);
    }

    public void render(ModelBatch batch, Shader shader) {
        batch.render(instance, shader);
    }

    public abstract void act();

    public abstract void applyTransform();

    public ModelInstance getInstance() {
        return instance;
    }

    @Override
    public void dispose() {
        this.model.dispose();
    }

    @Override
    public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
        this.instance.getRenderables(renderables, pool);
    }
}
