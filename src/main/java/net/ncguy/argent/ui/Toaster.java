package net.ncguy.argent.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.ToastManager;
import com.kotcrab.vis.ui.widget.toast.Toast;

import static net.ncguy.argent.ui.Toaster.ToastType.*;

/**
 * Created by Guy on 27/07/2016.
 */
public class Toaster {

    enum ToastType { SUCCESS, INFO, ERROR }

    private ToastManager toastManager;

    public Toaster(Stage stage) {
        this.toastManager = new ToastManager(stage);
    }

    public void info(String msg)    { toast(INFO,    3, msg); }
    public void error(String msg)   { toast(ERROR,   5, msg); }
    public void success(String msg) { toast(SUCCESS, 3, msg); }

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

}
