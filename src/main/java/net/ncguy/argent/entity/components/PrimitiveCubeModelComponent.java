package net.ncguy.argent.entity.components;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import net.ncguy.argent.editor.widgets.component.ComponentWidget;
import net.ncguy.argent.entity.WorldEntity;

import static com.badlogic.gdx.graphics.VertexAttributes.Usage.*;

/**
 * Created by Guy on 29/07/2016.
 */
@ComponentData(name = "Cube Primitive", limit = -1)
public class PrimitiveCubeModelComponent extends ModelComponent {

    public float width = 1;
    public float height = 1;
    public float depth = 1;
    public Material mtl;

    public PrimitiveCubeModelComponent(WorldEntity entity) {
        super(entity);
        mtl = new Material();
    }

    public void applyModel() {
        Model model = new ModelBuilder().createBox(width, height, depth, mtl, Position | Normal | TextureCoordinates);
        setModel(model);
    }

    @Override
    public Class<? extends ComponentWidget> widgetClass() {
        return null;
    }
}
