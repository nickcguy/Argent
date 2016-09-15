package net.ncguy.argent.entity.components.light;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import net.ncguy.argent.editor.widgets.component.ComponentWidget;
import net.ncguy.argent.editor.widgets.component.light.PointLightWidget;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.entity.components.ComponentData;

/**
 * Created by Guy on 14/09/2016.
 */
@ComponentData(name = "Point Light", limit = -1)
public class PointLightComponent extends LightComponent {

    float constant;
    float linear;
    float quadratic;

    public PointLightComponent(WorldEntity entity) {
        super(entity);
        constant = linear = quadratic = 0;
    }

    public float getConstant() {
        return constant;
    }

    public void setConstant(float constant) {
        this.constant = constant;
    }

    public float getLinear() {
        return linear;
    }

    public void setLinear(float linear) {
        this.linear = linear;
    }

    public float getQuadratic() {
        return quadratic;
    }

    public void setQuadratic(float quadratic) {
        this.quadratic = quadratic;
    }

    @Override
    public Class<? extends ComponentWidget> widgetClass() {
        return PointLightWidget.class;
    }

    @Override
    public void bind(ShaderProgram program, String prefix) {
        super.bind(program, prefix);
        bindFloat(program, prefix+".Constant", getConstant());
        bindFloat(program, prefix+".Linear", getLinear());
        bindFloat(program, prefix+".Quadratic", getQuadratic());
    }
}
