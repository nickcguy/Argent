package net.ncguy.argent.editor.widgets.component;

import net.ncguy.argent.entity.components.ArgentComponent;

/**
 * Created by Guy on 27/07/2016.
 */
public abstract class ComponentWidget<T extends ArgentComponent> extends BaseInspectorWidget {

    public T component;

    public ComponentWidget(T component) {
        this(component, component.getName());
    }

    public ComponentWidget(T component, String title) {
        super(title);
        this.component = component;
        setDeletable(true);
    }

    @Override
    public void onDelete() {
        component.remove();
        remove();
    }
}
