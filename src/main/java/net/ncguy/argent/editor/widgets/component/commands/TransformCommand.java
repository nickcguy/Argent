package net.ncguy.argent.editor.widgets.component.commands;

import net.ncguy.argent.Argent;
import net.ncguy.argent.editor.CommandHistory;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.event.WorldEntityModifiedEvent;

/**
 * Created by Guy on 27/07/2016.
 */
public abstract class TransformCommand<T> implements CommandHistory.Command {

    private static WorldEntityModifiedEvent modEvent = new WorldEntityModifiedEvent();

    private T before;
    private T after;
    protected WorldEntity we;

    public TransformCommand(WorldEntity we) {
        this.before = null;
        this.after = null;
        this.we = we;
    }

    public void setBefore(T before) { this.before = before; }
    public void setAfter(T after) { this.after = after; }
    public void setWe(WorldEntity we) { this.we = we; }

    protected abstract void executeInternal(T target);

    @Override
    public void execute() {
        executeInternal(after);
        modEvent.setEntity(we);
        Argent.event.post(modEvent);
    }

    @Override
    public void undo() {
        executeInternal(before);
        modEvent.setEntity(we);
        Argent.event.post(modEvent);
    }

    @Override
    public void dispose() {
        this.before = null;
        this.after = null;
        this.we = null;
    }
}
