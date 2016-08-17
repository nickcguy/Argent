package net.ncguy.argent.entity.components.model;

import com.badlogic.gdx.graphics.g3d.Model;
import net.ncguy.argent.Argent;
import net.ncguy.argent.editor.widgets.component.ComponentWidget;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.entity.components.ComponentData;

/**
 * Created by Guy on 29/07/2016.
 */
@ComponentData(name = "Complex Model")
public class ComplexModelComponent extends ModelComponent {

    String modelRef;

    public ComplexModelComponent(WorldEntity entity) {
        super(entity);
    }

    public String getModelRef() { return modelRef; }

    public void setModelRef(String ref) {
        Model model = Argent.content.get(ref, Model.class);
        if(model == null) return;
        setModel(model);
    }


    @Override
    public Class<? extends ComponentWidget> widgetClass() {
        return null;
    }
}
