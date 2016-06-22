package net.ncguy.argent.command;

import com.badlogic.gdx.math.MathUtils;
import com.strongjoshua.console.CommandExecutor;
import com.strongjoshua.console.Console;
import net.ncguy.argent.Argent;
import net.ncguy.argent.editor.ConfigurableAttribute;
import net.ncguy.argent.editor.IConfigurable;
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

    public void exposure(float exposure) {
        exposure = (float) MathUtils.clamp(exposure, 0.1, 10);
        Argent.GlobalConfig.exposure = exposure;
    }

    public void attr(String key) {
        Object obj = gameWorld.selected();
        if(obj instanceof IConfigurable) {
            IConfigurable cfg = (IConfigurable)obj;
            for (ConfigurableAttribute attr : cfg.getConfigurableAttributes()) {
                if(attr.displayName().equalsIgnoreCase(key)) {
                    console.log(attr.getter().run().toString());
                    return;
                }
            }
        }
    }

    public void attr(String key, String val) {
        Object obj = gameWorld.selected();
        if(obj instanceof IConfigurable) {
            IConfigurable cfg = (IConfigurable)obj;
            for (ConfigurableAttribute attr : cfg.getConfigurableAttributes()) {
                if(attr.displayName().equalsIgnoreCase(key)) {
                    attr.setter().run(val);
                    console.log(attr.getter().run().toString());
                    return;
                }
            }
        }
    }

    public void clear() {
        this.console.clear();
    }

}
