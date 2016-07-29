package net.ncguy.argent.editor.widgets.component.commands;

import net.ncguy.argent.entity.WorldEntity;

/**
 * Created by Guy on 29/07/2016.
 */
public class NameCommand extends TransformCommand<String> {

    public NameCommand(WorldEntity we) {
        super(we);
    }

    @Override
    protected void executeInternal(String target) {
        we.name = target;
    }
}
