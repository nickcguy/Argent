package net.ncguy.argent.entity.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import net.ncguy.argent.Argent;
import net.ncguy.argent.editor.widgets.component.ComponentWidget;
import net.ncguy.argent.editor.widgets.component.LightWidget;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.utils.AppUtils;

import static net.ncguy.argent.Argent.VarKeys.bool_LIGHTDEBUG;

/**
 * Created by Guy on 29/07/2016.
 */
@ComponentData(name = "Light")
public class LightComponent implements ArgentComponent {

    WorldEntity entity;

    Vector3 localPosition;
    Color colour;
    float linear, quadratic, intensity;
    boolean inverse = false;
    float radius;

    private Model debugModel;
    private ModelInstance debugInstance;
    private ModelInstance debugInstance() {
        if (debugInstance == null) {
            debugModel = new ModelBuilder().createBox(.1f, .1f, .1f, new Material(ColorAttribute.createDiffuse(AppUtils.Graphics.randomColour())), VertexAttributes.Usage.Position);
            debugInstance = new ModelInstance(debugModel);
        }
        debugInstance.transform.setToTranslation(localPosition);
        return debugInstance;
    }

    public LightComponent(WorldEntity entity) {
        this.entity = entity;
        this.localPosition = new Vector3(0, 1.2f, 0);
        this.colour = new Color();
        this.linear = 1;
        this.quadratic = 1;
        this.intensity = 1;
    }

    @Override
    public WorldEntity getWorldEntity() {
        return null;
    }

    @Override public void update(float delta) {
        Gdx.graphics.setTitle(getLocalPosition().toString());
    }

    @Override
    public Type getType() {
        return Type.LIGHT;
    }

    @Override
    public void setType(Type type) {
        // NOOP
    }

    @Override
    public void remove() {
        this.entity.remove(this);
    }

    @Override
    public Class<? extends ComponentWidget> widgetClass() {
        return LightWidget.class;
    }

    public Vector3 getLocalPosition() {
        return localPosition;
    }

    public Vector3 getWorldPosition() {
        Vector3 pos = entity.getLocalPosition(new Vector3());
        return pos.add(getLocalPosition());
    }

    public Color getColour() {
        return colour;
    }

    public float getLinear() {
        return linear;
    }

    public float getQuadratic() {
        return quadratic;
    }

    public void setLinear(float linear) {
        this.linear = linear;
    }

    public void setQuadratic(float quadratic) {
        this.quadratic = quadratic;
    }

    public float getIntensity() { return intensity; }

    public void setIntensity(float intensity) { this.intensity = intensity; }

    @Override
    public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
        if(Argent.hasBoolVar(bool_LIGHTDEBUG))
            debugInstance().getRenderables(renderables, pool);
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    @Override
    public void dispose() {
        if(debugInstance != null) {
            debugInstance = null;
        }
        if(debugModel != null) {
            debugModel.dispose();
            debugModel = null;
        }
    }
}
