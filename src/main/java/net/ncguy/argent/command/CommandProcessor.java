package net.ncguy.argent.command;

import com.strongjoshua.console.CommandExecutor;
import com.strongjoshua.console.Console;
import net.ncguy.argent.world.GameWorld;

/**
 * Created by Guy on 21/06/2016.
 */
public class CommandProcessor extends CommandExecutor {

    protected GameWorld.Generic<?> gameWorld;

    public CommandProcessor(GameWorld.Generic<?> gameWorld, Console console) {
        this.gameWorld = gameWorld;
        this.console = console;
    }

    public void displayBuffers() {
        this.gameWorld.showBuffers = !this.gameWorld.showBuffers;
        if(this.gameWorld.showBuffers)
            console.log("Drawing buffers");
        else console.log("Hiding buffers");
    }

    public void clear() {
        this.console.clear();
    }

}
