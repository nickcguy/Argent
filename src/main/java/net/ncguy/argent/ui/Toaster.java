package net.ncguy.argent.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.ToastManager;
import com.kotcrab.vis.ui.widget.toast.Toast;
import net.ncguy.argent.Argent;
import net.ncguy.argent.event.StringPacketEvent;

import static net.ncguy.argent.ui.Toaster.ToastType.*;

/**
 * Created by Guy on 27/07/2016.
 */
public class Toaster implements StringPacketEvent.StringPacketListener {

    public enum ToastType { SUCCESS, INFO, ERROR }

    private ToastManager toastManager;

    public Toaster(Stage stage) {
        this.toastManager = new ToastManager(stage);
        Argent.event.register(this);
    }

    public void info(String msg)    { toast(INFO,    3, msg); }
    public void error(String msg)   { toast(ERROR,   5, msg); }
    public void success(String msg) { toast(SUCCESS, 3, msg); }

    public void info(String format, String... args)    {
        toast(INFO, 3, String.format(format, args));
    }
    public void error(String format, String... args)   {
        toast(ERROR, 5, String.format(format, args));
    }
    public void success(String format, String... args) {
        toast(SUCCESS, 3, String.format(format, args));
    }

    public void sticky(ToastType type, String msg) { toast(type, ToastManager.UNTIL_CLOSED, msg); }

    public void toast(ToastType type, int timeSec, String msg) {
        final Table table = newTable(msg);
        final Toast toast = new Toast(type.name().toLowerCase(), table);
        toastManager.show(toast, timeSec);
    }

    private Table newTable(String msg) {
        final Table table = new Table(VisUI.getSkin());
        table.add(msg);
        table.pad(5);
        return table;
    }

    @Override
    public void onStringPacket(StringPacketEvent event) {
        String[] keys = event.key.split("\\|");
        if(keys.length >= 2) {
            if(!keys[0].replace(" ", "").equalsIgnoreCase("toast")) return;
            switch(keys[1].toLowerCase().replace(" ", "")) {
                case "info": info(event.payload); break;
                case "error": error(event.payload); break;
                case "success": success(event.payload); break;
            }
        }
    }

}
