package net.ncguy.argent.editor.widgets.component.light;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.spinner.SimpleFloatSpinnerModel;
import com.kotcrab.vis.ui.widget.spinner.Spinner;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.entity.components.light.SpotLightComponent;
import net.ncguy.argent.ui.widget.Vector3Selection;

import java.util.function.Consumer;

/**
 * Created by Guy on 14/09/2016.
 */
public class SpotLightWidget extends LightWidget<SpotLightComponent> {

    Vector3Selection position;
    Vector3Selection direction;
    Spinner c, l, q;
    SimpleFloatSpinnerModel cM, lM, qM;

    Spinner ico, oco;
    SimpleFloatSpinnerModel icoM, ocoM;

    public SpotLightWidget(SpotLightComponent component) {
        super(component, "Spot Light");
    }

    @Override
    protected void init() {
        super.init();
        position = new Vector3Selection();
        direction = new Vector3Selection(true);

        cM = new SimpleFloatSpinnerModel(1, -1024, 1024, .1f, 2);
        lM = new SimpleFloatSpinnerModel(1, -1024, 1024, .1f, 2);
        qM = new SimpleFloatSpinnerModel(1, -1024, 1024, .1f, 2);

        c = new Spinner("", cM);
        l = new Spinner("", lM);
        q = new Spinner("", qM);

        icoM = new SimpleFloatSpinnerModel(1, -1024, 1024, 1, 1);
        ocoM = new SimpleFloatSpinnerModel(1, -1024, 1024, 1, 1);

        ico = new Spinner("", icoM);
        oco = new Spinner("", ocoM);
    }

    @Override
    protected void setupUI() {
        collapsibleContent.add("Position").padRight(5).padBottom(4).left();
        collapsibleContent.add(position).padBottom(4).growX().row();

        collapsibleContent.add("Direction").padRight(5).padBottom(4).left();
        collapsibleContent.add(direction).padBottom(4).growX().row();

        collapsibleContent.add("Constant").padRight(5).padBottom(4).left();
        collapsibleContent.add(c).padBottom(4).growX().row();

        collapsibleContent.add("Linear").padRight(5).padBottom(4).left();
        collapsibleContent.add(l).padBottom(4).growX().row();

        collapsibleContent.add("Quadratic").padRight(5).padBottom(4).left();
        collapsibleContent.add(q).padBottom(4).growX().row();

        collapsibleContent.add("Cut Off").padRight(5).padBottom(4).left();
        collapsibleContent.add(ico).padBottom(4).growX().row();

        collapsibleContent.add("Outer Cut off").padRight(5).padBottom(4).left();
        collapsibleContent.add(oco).padBottom(4).growX().row();

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
        direction.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                component.getDirection().set(direction.getRawDirection());
            }
        });
        c.addListener(onChange(cM, component::setConstant));
        l.addListener(onChange(lM, component::setLinear));
        q.addListener(onChange(qM, component::setQuadratic));

        ico.addListener(onChange(icoM, component::setCutoff));
        oco.addListener(onChange(ocoM, component::setOuterCutoff));
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
        direction.set(component.getDirection());
        cM.setValue(component.getConstant(), false);
        lM.setValue(component.getLinear(), false);
        qM.setValue(component.getQuadratic(), false);
        icoM.setValue(component.getCutoff(), false);
        ocoM.setValue(component.getOuterCutoff(), false);
        super.setValues(entity);
    }
}
