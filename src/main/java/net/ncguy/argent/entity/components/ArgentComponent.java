package net.ncguy.argent.entity.components;

import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.utils.Disposable;
import net.ncguy.argent.editor.widgets.component.ComponentWidget;
import net.ncguy.argent.entity.WorldEntity;

/**
 * Created by Guy on 23/07/2016.
 */
public interface ArgentComponent extends RenderableProvider, Disposable {

    public static enum Type {
        MODEL, TERRAIN, LIGHT, PARTICLE_SYSTEM,
    }

    WorldEntity getWorldEntity();
    void update(float delta);
    Type getType();
    void setType(Type type);
    void remove();

    Class<? extends ComponentWidget> widgetClass();

    @Override
    default void dispose() {}
}
