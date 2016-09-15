package net.ncguy.argent.entity.components.light;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import net.ncguy.argent.editor.widgets.component.ComponentWidget;
import net.ncguy.argent.editor.widgets.component.light.SpotLightWidget;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.entity.components.ComponentData;

/**
 * Created by Guy on 14/09/2016.
 */
@ComponentData(name = "Spot Light", limit = -1)
public class SpotLightComponent extends LightComponent {

    Vector3 direction;

    float constant;
    float linear;
    float quadratic;

    float cutoff;
    float outerCutoff;

    public SpotLightComponent(WorldEntity entity) {
        super(entity);
        direction = new Vector3();
        constant = linear = quadratic = 0;
        cutoff = outerCutoff = 0;
    }

    public Vector3 getDirection() {
        return direction;
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

    public float getCutoff() {
        return cutoff;
    }

    public void setCutoff(float cutoff) {
        this.cutoff = cutoff;
    }

    public float getOuterCutoff() {
        return outerCutoff;
    }

    public void setOuterCutoff(float outerCutoff) {
        this.outerCutoff = outerCutoff;
    }

    @Override
    public Class<? extends ComponentWidget> widgetClass() {
        return SpotLightWidget.class;
    }

    @Override
    public void bind(ShaderProgram program, String prefix) {
        super.bind(program, prefix);
        bindFloat(program, prefix+".Constant", getConstant());
        bindFloat(program, prefix+".Linear", getLinear());
        bindFloat(program, prefix+".Quadratic", getQuadratic());
        bindFloat(program, prefix+".CutOff", getCutoff());
        bindFloat(program, prefix+".OuterCutOff", getOuterCutoff());
    }

}
