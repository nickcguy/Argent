package net.ncguy.argent.entity.components.light;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import net.ncguy.argent.editor.widgets.component.ComponentWidget;
import net.ncguy.argent.editor.widgets.component.light.DirLightWidget;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.entity.components.ComponentData;
import net.ncguy.argent.utils.AppUtils;

/**
 * Created by Guy on 14/09/2016.
 */
@ComponentData(name = "Directional Light")
public class DirLightComponent extends LightComponent {

    Vector3 direction;

    public DirLightComponent(WorldEntity entity) {
        super(entity);
        this.direction = new Vector3();
    }

    @Override
    protected ModelInstance debugInstance() {
        if (debugInstance == null) {
            if(direction.equals(Vector3.Zero))
                debugModel = new ModelBuilder().createBox(.1f, .1f, .1f, new Material(ColorAttribute.createDiffuse(AppUtils.Graphics.randomColour())), VertexAttributes.Usage.Position);
            else
                debugModel = new ModelBuilder().createArrow(getWorldPosition(), getWorldPosition().cpy().add(direction), new Material(ColorAttribute.createDiffuse(AppUtils.Graphics.randomColour())), VertexAttributes.Usage.Position);
            debugInstance = new ModelInstance(debugModel);
        }
        return debugInstance;
    }

    @Override
    public void bind(ShaderProgram program, String prefix) {
        super.bind(program, prefix);
        bindVector3(program, prefix+".Direction", getDirection());
    }

    public Vector3 getDirection() {
        return direction;
    }

    public void setDirection(Vector3 direction) {
        this.direction.set(direction);
        invalidateDebug();
    }

    @Override
    public Class<? extends ComponentWidget> widgetClass() {
        return DirLightWidget.class;
    }

    @Override
    public boolean usePosition() {
        return false;
    }
}
