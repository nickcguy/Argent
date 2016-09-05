package net.ncguy.argent.entity.components.model.primitive;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import net.ncguy.argent.assets.ArgShader;
import net.ncguy.argent.editor.widgets.component.ComponentWidget;
import net.ncguy.argent.editor.widgets.component.PrimitiveSphereModelWidget;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.entity.components.ComponentData;
import net.ncguy.argent.entity.components.model.ModelComponent;

/**
 * Created by Guy on 04/08/2016.
 */
@ComponentData(name = "Sphere Primitive", limit = -1)
public class PrimitiveSphereModelComponent extends ModelComponent {

    public float width = 1;
    public float height = 1;
    public float depth = 1;
    public int divU = 20;
    public int divV = 20;

    public PrimitiveSphereModelComponent(WorldEntity entity) {
        super(entity);
        applyModel();
    }

    public PrimitiveSphereModelComponent(WorldEntity entity, ArgShader shader) {
        super(entity, shader);
        applyModel();
    }

    public Vector3 get() {
        return new Vector3(width, height, depth);
    }
    public Vector2 getDivs() {
        return new Vector2(divU, divV);
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

    public void set(Vector2 divs) {
        set((int)divs.x, (int)divs.y);
    }
    public void set(int u, int v) {
        this.divU = u;
        this.divV = v;
        applyModel();
    }

    public void set(Vector3 dims, Vector2 divs) {
        set(dims.x, dims.y, dims.z, (int)divs.x, (int)divs.y);
    }
    public void set(float w, float h, float d, int u, int v) {
        this.width  = w;
        this.height = h;
        this.depth  = d;
        this.divU   = u;
        this.divV   = v;
        applyModel();
    }

    public void applyModel() {
        Model model = new ModelBuilder().createSphere(width, height, depth, divU, divV, new Material(), attributes);
        setModel(model);
    }

    @Override
    public Class<? extends ComponentWidget> widgetClass() {
        return PrimitiveSphereModelWidget.class;
    }

}
