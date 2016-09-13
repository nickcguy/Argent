package net.ncguy.argent.entity.components.material;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import net.ncguy.argent.assets.ArgShader;
import net.ncguy.argent.editor.widgets.component.ArgShaderWidget;
import net.ncguy.argent.editor.widgets.component.ComponentWidget;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.entity.components.ArgentComponent;
import net.ncguy.argent.entity.components.ComponentData;

/**
 * Created by Guy on 13/09/2016.
 */
@ComponentData(name = "Shader", limit = 1)
public class ShaderComponent implements ArgentComponent {

    WorldEntity entity;
    ArgShader shader;

    public ShaderComponent(WorldEntity entity) {
        this.entity = entity;
    }

    public void setShader(ArgShader shader) {
        this.shader = shader;
        getWorldEntity().modelComponents().forEach(model -> model.setMaterial(this.shader));
    }

    public ArgShader getShader() {
        return shader;
    }

    @Override
    public WorldEntity getWorldEntity() {
        return entity;
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public Type getType() {
        return null;
    }

    @Override
    public void setType(Type type) {

    }

    @Override
    public void remove() {
        setShader(null);
        getWorldEntity().remove(this);
    }

    @Override
    public Class<? extends ComponentWidget> widgetClass() {
        return ArgShaderWidget.class;
    }

    @Override
    public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {

    }
}
