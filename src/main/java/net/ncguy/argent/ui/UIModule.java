package net.ncguy.argent.ui;

import com.badlogic.gdx.files.FileHandle;
import com.kotcrab.vis.ui.VisUI;
import net.ncguy.argent.IModule;
import net.ncguy.argent.content.ContentModule;

/**
 * Created by Guy on 17/07/2016.
 */
public class UIModule extends IModule {

    private static FileHandle handle = VisUI.SkinScale.X1.getSkinFile();

    public static FileHandle handle() { return handle; }
    public static void handle(FileHandle handle) { UIModule.handle = handle; }

    public UIModule() {
        VisUI.load(handle);
        Icons.init();
    }

    @Override
    public Class<IModule>[] dependencies() {
        return new Class[]{ContentModule.class};
    }

    @Override
    public String moduleName() {
        return "User Interface";
    }

}
