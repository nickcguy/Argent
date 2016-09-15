package net.ncguy.argent.editor.widgets.component.light;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.spinner.SimpleFloatSpinnerModel;
import com.kotcrab.vis.ui.widget.spinner.Spinner;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.entity.components.light.PointLightComponent;
import net.ncguy.argent.ui.widget.Vector3Selection;

import java.util.function.Consumer;

/**
 * Created by Guy on 14/09/2016.
 */
public class PointLightWidget extends LightWidget<PointLightComponent> {

    Vector3Selection position;
    Spinner c, l, q;
    SimpleFloatSpinnerModel cM, lM, qM;

    public PointLightWidget(PointLightComponent component) {
        super(component, "Point Light");
    }

    @Override
    protected void init() {
        super.init();
        position = new Vector3Selection();

        cM = new SimpleFloatSpinnerModel(1, -1024, 1024, .1f, 2);
        lM = new SimpleFloatSpinnerModel(1, -1024, 1024, .1f, 2);
        qM = new SimpleFloatSpinnerModel(1, -1024, 1024, .1f, 2);

        c = new Spinner("", cM);
        l = new Spinner("", lM);
        q = new Spinner("", qM);
    }

    @Override
    protected void setupUI() {
        collapsibleContent.add("Position").padRight(5).padBottom(4).left();
        collapsibleContent.add(position).padBottom(4).growX().row();

        collapsibleContent.add("Constant").padRight(5).padBottom(4).left();
        collapsibleContent.add(c).padBottom(4).growX().row();

        collapsibleContent.add("Linear").padRight(5).padBottom(4).left();
        collapsibleContent.add(l).padBottom(4).growX().row();

        collapsibleContent.add("Quadratic").padRight(5).padBottom(4).left();
        collapsibleContent.add(q).padBottom(4).growX().row();

        super.setupUI();
    }

    @Override
    protected void setupListeners() {
        position.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                component.getLocalPosition().set(position.getRawDirection());
            }
        });
        c.addListener(onChange(cM, component::setConstant));
        l.addListener(onChange(lM, component::setLinear));
        q.addListener(onChange(qM, component::setQuadratic));
        super.setupListeners();
    }

    private ChangeListener onChange(SimpleFloatSpinnerModel model, Consumer<Float> action) {
        return new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                action.accept(model.getValue());
            }
        };
    }

    @Override
    public void setValues(WorldEntity entity) {
        position.set(component.getLocalPosition());
        cM.setValue(component.getConstant());
        lM.setValue(component.getLinear());
        qM.setValue(component.getQuadratic());
        super.setValues(entity);
    }
}
