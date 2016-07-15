package net.ncguy.argent.command;

import aurelienribon.tweenengine.Tween;
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

    public void exposure() {
        console.log(String.valueOf(Argent.GlobalConfig.exposure.floatValue()));
    }

    public void exposure(float exposure) {
        Tween.to(Argent.GlobalConfig.exposure, 0, .3f).target(exposure).start(Argent.tweenManager);
    }

    public void brightness() {
        console.log(String.valueOf(Argent.GlobalConfig.brightness.floatValue()));
    }

    public void brightness(float brightness) {
        Tween.to(Argent.GlobalConfig.brightness, 0, .3f).target(brightness).start(Argent.tweenManager);
    }

    public void ambient() {
        console.log(String.valueOf(Argent.GlobalConfig.ambient.floatValue()));
    }
    public void ambient(float ambient) {
        Tween.to(Argent.GlobalConfig.ambient, 0, .3f).target(ambient).start(Argent.tweenManager);
    }

    public void attr(String key) {
        Object obj = gameWorld.selected();
        if(obj instanceof IConfigurable) {
            IConfigurable cfg = (IConfigurable)obj;
            System.out.println(key);
            for (ConfigurableAttribute attr : cfg.getConfigAttrs()) {
                if(attr.displayName().replace(" ", "_").equalsIgnoreCase(key)) {
                    console.log(attr.get().toString());
                    return;
                }
            }
        }
    }

    public void attr(String key, String val) {
        Object obj = gameWorld.selected();
        if(obj instanceof IConfigurable) {
            IConfigurable cfg = (IConfigurable)obj;
            for (ConfigurableAttribute attr : cfg.getConfigAttrs()) {
                if(attr.displayName().replace(" ", "_").equalsIgnoreCase(key)) {
                    attr.set(val);
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
