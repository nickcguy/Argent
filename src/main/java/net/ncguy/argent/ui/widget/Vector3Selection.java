package net.ncguy.argent.ui.widget;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.spinner.SimpleFloatSpinnerModel;
import com.kotcrab.vis.ui.widget.spinner.Spinner;

/**
 * Created by Guy on 14/09/2016.
 */
public class Vector3Selection extends Table {

    boolean normalize;

    public Vector3Selection() {
        this(false);
    }

    public Vector3Selection(boolean normalize) {
        super(VisUI.getSkin());
        this.normalize = normalize;
        construct();
    }

    private void construct() {
        constructStandard();
    }

    // STANDARD

    Spinner x, y, z;
    SimpleFloatSpinnerModel xM, yM, zM;
    Vector3 tmp;

    private void constructStandard() {
        tmp = new Vector3();
        xM = new SimpleFloatSpinnerModel(0, -1024, 1024, 0.05f, 2);
        yM = new SimpleFloatSpinnerModel(0, -1024, 1024, 0.05f, 2);
        zM = new SimpleFloatSpinnerModel(0, -1024, 1024, 0.05f, 2);
        x = new Spinner("X", xM);
        y = new Spinner("Y", yM);
        z = new Spinner("Z", zM);
        standard_attachListeners();
        standard_assemble();
    }

    private void standard_attachListeners() {
        FocusListener focusListener = new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
                if(actor == x || actor == y || actor == z) return;
                normalize();
            }
        };
        x.addListener(focusListener);
        y.addListener(focusListener);
        z.addListener(focusListener);
    }

    private void standard_assemble() {
        add(x).growX().row();
        add(y).growX().row();
        add(z).growX().row();
    }

    public Vector3Selection normalize() {
        tmp.set(xM.getValue(), yM.getValue(), zM.getValue());
        if(normalize) tmp.nor();
        xM.setValue(tmp.x);
        yM.setValue(tmp.y);
        zM.setValue(tmp.z);
        fire(new DirectionChangeEvent());
        return this;
    }

    public Vector3 getDirection() {
        normalize();
        return tmp.cpy();
    }

    public Vector3 getRawDirection() {
        return tmp.cpy();
    }

    public void set(Vector3 vec) {
        xM.setValue(vec.x, false);
        yM.setValue(vec.y, false);
        zM.setValue(vec.z, false);
    }

    // EXPERIMENTAL

    private void constructExperimental() {
        // TODO draw sphere
        // TODO allow to select point on sphere surface
        // TODO display point as {@link Vector3}
    }

    public static class DirectionChangeEvent extends ChangeListener.ChangeEvent {

    }

}
