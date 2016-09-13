package net.ncguy.argent.editor.widgets.component;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.kotcrab.vis.ui.VisUI;
import net.ncguy.argent.assets.ArgShader;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.entity.components.material.ShaderComponent;
import net.ncguy.argent.ui.dnd.DropZone;

/**
 * Created by Guy on 13/09/2016.
 */
public class ArgShaderWidget extends ComponentWidget<ShaderComponent> {

    DropZone<ArgShader> mtlDropZone;
    Label mtlLabel;

    public ArgShaderWidget(ShaderComponent component) {
        super(component, "Shader");
        setDeletable(true);
        init();
        setupUI();
        setupListeners();
    }

    private void init() {
        mtlDropZone = new DropZone<>(ArgShader.class, "shader");
        mtlLabel = new Label("", VisUI.getSkin());
    }

    protected void setupUI() {
        collapsibleContent.add("Shader").padRight(5).padBottom(4).left().colspan(2).row();
        collapsibleContent.add(mtlLabel).padRight(5).padBottom(4).left();
        collapsibleContent.add(mtlDropZone).growX().height(32).right().row();
    }

    protected void setupListeners() {
        mtlDropZone.setOnDrop(this::setComponentShader);
    }

    private void setComponentShader(ArgShader argShader) {
        mtlLabel.setText(argShader.name());
        component.setShader(argShader);
    }

    @Override
    public void setValues(WorldEntity entity) {
        if(component == null) return;
        if(component.getShader() == null) return;
        mtlLabel.setText(component.getShader().name());
    }
}
