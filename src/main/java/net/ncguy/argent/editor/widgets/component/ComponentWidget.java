package net.ncguy.argent.editor.widgets.component;

import net.ncguy.argent.entity.components.ArgentComponent;

/**
 * Created by Guy on 27/07/2016.
 */
public abstract class ComponentWidget<T extends ArgentComponent> extends BaseInspectorWidget {

    public T component;

    public ComponentWidget(String title) {
        super(title);
    }

    @Override
    public void onDelete() {
        component.remove();
        remove();
    }
}
