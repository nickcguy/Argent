package net.ncguy.argent.entity.components.model.primitive;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import net.ncguy.argent.assets.ArgMaterial;
import net.ncguy.argent.editor.widgets.component.ComponentWidget;
import net.ncguy.argent.editor.widgets.component.PrimitiveCubeModelWidget;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.entity.components.ComponentData;
import net.ncguy.argent.entity.components.model.ModelComponent;

import static com.badlogic.gdx.graphics.VertexAttributes.Usage.*;

/**
 * Created by Guy on 29/07/2016.
 */
@ComponentData(name = "Cube Primitive", limit = -1)
public class PrimitiveCubeModelComponent extends ModelComponent {

    public float width = 1;
    public float height = 1;
    public float depth = 1;

    public PrimitiveCubeModelComponent(WorldEntity entity) {
        super(entity);
        applyModel();
    }

    public PrimitiveCubeModelComponent(WorldEntity entity, ArgMaterial mtl) {
        super(entity, mtl);
        applyModel();
    }

    public Vector3 get() {
        return new Vector3(width, height, depth);
    }

    public void set(Vector3 dims) {
        set(dims.x, dims.y, dims.z);
    }
    public void set(float width, float height, float depth) {
        this.width  = width;
        this.height = height;
        this.depth  = depth;
        applyModel();
    }

    public void applyModel() {
        Model model = new ModelBuilder().createBox(width, height, depth, mtl.getAsset(), Position | Normal | TextureCoordinates);
        setModel(model);
    }

    @Override
    public Class<? extends ComponentWidget> widgetClass() {
        return PrimitiveCubeModelWidget.class;
    }


}
