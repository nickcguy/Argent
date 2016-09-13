package net.ncguy.argent.editor.widgets.component;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.VisUI;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.entity.components.model.primitive.PrimitivePlaneModelComponent;
import net.ncguy.argent.ui.FloatFieldWithLabel;

/**
 * Created by Guy on 16/08/2016.
 */
public class PrimitivePlaneModelWidget extends ComponentWidget<PrimitivePlaneModelComponent> {

    private TextField name;
    private FloatFieldWithLabel width;
    private FloatFieldWithLabel height;
    private CheckBox twoSided;
//    private DropZone<ArgShader> mtlDropZone;

    public PrimitivePlaneModelWidget(PrimitivePlaneModelComponent component) {
        super(component, "Plane Primitive");
        setDeletable(true);
        init();
        setupUI();
        setupListeners();
    }

    protected void init() {
        int size = 64;
        name = new TextField("", VisUI.getSkin());
        width = new FloatFieldWithLabel("Width", size, false);
        height = new FloatFieldWithLabel("Height", size, false);
        twoSided = new CheckBox("Two Sided", VisUI.getSkin());
//        mtlDropZone = new DropZone<>(ArgShader.class, "shader");
//        mtlDropZone.setOnDrop(component::setMaterial);
    }
    protected void setupUI() {
        int pad = 4;

        collapsibleContent.add("Size").padRight(5).padBottom(pad).left();
        collapsibleContent.add(width).padRight(5).padBottom(pad);
        collapsibleContent.add(height).padRight(5).padBottom(pad).row();

        collapsibleContent.add(twoSided).colspan(3).padBottom(pad).row();

        collapsibleContent.add("Name").padRight(5).padBottom(pad).left();
        collapsibleContent.add(name).padBottom(pad).colspan(2).expandX().fillX().row();

//        collapsibleContent.add("Material").padRight(5).padBottom(pad).left();
//        collapsibleContent.add(mtlDropZone).padBottom(pad).colspan(3).expandX().fillX().height(64).row();
    }
    protected void setupListeners() {
        name.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                component.name(name.getText());
            }
        });

        ChangeListener dimsListener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                component.set(getDims());
            }
        };
        width.addListener(dimsListener);
        height.addListener(dimsListener);

        twoSided.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                component.setTwoSided(twoSided.isChecked());
            }
        });
    }

    public Vector2 getDims() {
        return new Vector2(width.getFloat(), height.getFloat());
    }

    @Override
    public void setValues(WorldEntity entity) {
        name.setText(component.name());
        width.setText(component.width+"");
        height.setText(component.height+"");
        twoSided.setChecked(component.twoSided);
    }
}
