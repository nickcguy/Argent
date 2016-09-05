package net.ncguy.argent.entity.components.model.primitive;

import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector2;
import net.ncguy.argent.assets.ArgShader;
import net.ncguy.argent.editor.widgets.component.ComponentWidget;
import net.ncguy.argent.editor.widgets.component.PrimitivePlaneModelWidget;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.entity.components.ComponentData;
import net.ncguy.argent.entity.components.model.ModelComponent;

/**
 * Created by Guy on 16/08/2016.
 */
@ComponentData(name = "Plane Primitive", limit = -1)
public class PrimitivePlaneModelComponent extends ModelComponent {

    public float width = 1;
    public float height = 1;
    public boolean twoSided;

    public PrimitivePlaneModelComponent(WorldEntity entity) {
        super(entity);
        applyModel();
    }

    public PrimitivePlaneModelComponent(WorldEntity entity, ArgShader shader) {
        super(entity, shader);
        applyModel();
    }

    public Vector2 get() {
        return new Vector2(width, height);
    }

    public void set(Vector2 dims) {
        set(dims.x, dims.y);
    }
    public void set(float width, float height) {
        this.width  = width;
        this.height = height;
        applyModel();
    }

    public void applyModel() {

        /*
        float x00, float y00, float z00, float x10, float y10, float z10, float x11, float y11, float z11,
		float x01, float y01, float z01, float normalX, float normalY, float normalZ, int primitiveType, final Material material,
		final long attributes
         */

        float w = width/2;
        float h = height/2;
//        Model model = new ModelBuilder().createSphere(width, height, depth, divU, divV, shader.getAsset(), Position | Normal | TextureCoordinates);
        ModelBuilder mb = new ModelBuilder();
        mb.begin();
        mb.part("front", GL30.GL_TRIANGLES, attributes, new Material()).rect(-w, -h, 0, w, -h, 0, w, h, 0, -w, h, 0, 0, 0, 1);
        if(twoSided)
            mb.part("back", GL30.GL_TRIANGLES, attributes, new Material()).rect(w, -h, 0, -w, -h, 0, -w, h, 0, w, h, 0, 0, 0, -1);

        setModel(mb.end());
    }

    @Override
    public Class<? extends ComponentWidget> widgetClass() {
        return PrimitivePlaneModelWidget.class;
    }

    public void setTwoSided(boolean twoSided) {
        this.twoSided = twoSided;
        applyModel();
    }
}
