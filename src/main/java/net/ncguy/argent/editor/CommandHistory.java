package net.ncguy.argent.editor;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guy on 27/07/2016.
 */
public class CommandHistory {

    public static final int DEFAULT_LIMIT = 64;

    private int limit;
    private int pointer;
    private Array<Command> commands;

    public CommandHistory() {
        this(DEFAULT_LIMIT);
    }

    public CommandHistory(int limit) {
        this.limit = limit;
        this.commands = new Array<>(limit);
        this.pointer = -1;
    }

    public int add(Command cmd) {
        if(size() == 0) {
            commands.add(cmd);
            pointer++;
            return pointer;
        }

        if(pointer < size() - 1) {
            removeCommands(pointer + 1, size() - 1);
            commands.add(cmd);
            pointer++;
        }else{
            if(size() == limit) {
                removeCommand(0);
                commands.add(cmd);
            }else{
                commands.add(cmd);
                pointer++;
            }
        }
        return pointer;
    }

    private void removeCommand(int index) {
        commands.get(index).dispose();
        commands.removeIndex(index);
    }

    private void removeCommands(int from, int to) {
        for(int i = from; i <= to; i++)
            commands.get(i).dispose();
        commands.removeRange(from, to);
    }

    public int goBack() {
        if(pointer >= 0)
            commands.get(pointer--).undo();
        return pointer;
    }

    public int goForward() {
        if(pointer < size() - 1) {
            pointer++;
            commands.get(pointer).execute();
        }
        return pointer;
    }

    public void goTo(int index) {
        if(pointer > index) {
            while(goBack() != pointer);
        }else{
            while(goForward() != pointer);
        }
    }

    public List<Command> commands() {
        List<Command> cmds = new ArrayList<>();
        commands.forEach(cmds::add);
        return cmds;
    }

    public int getPointer() { return pointer; }
    public int size() { return commands.size; }

    public static interface Command extends Disposable {
        void execute();
        void undo();
    }
}
