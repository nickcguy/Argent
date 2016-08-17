package net.ncguy.argent.editor.widgets.component.commands;

import net.ncguy.argent.entity.WorldEntity;

import java.util.function.BiConsumer;

/**
 * Created by Guy on 30/07/2016.
 */
public class BasicCommand<T> extends TransformCommand<T> {

    BiConsumer<WorldEntity, T> command;

    public BasicCommand(WorldEntity we, BiConsumer<WorldEntity, T> command) {
        super(we);
        this.command = command;
    }

    @Override
    protected void executeInternal(T target) {
        this.command.accept(we, target);
    }

}
