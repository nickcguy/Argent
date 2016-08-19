package net.ncguy.argent.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.kotcrab.vis.ui.VisUI;
import net.ncguy.argent.IModule;
import net.ncguy.argent.content.ContentModule;

/**
 * Created by Guy on 17/07/2016.
 */
public class UIModule extends IModule {

//    private static FileHandle handle = VisUI.SkinScale.X1.getSkinFile();
    private static FileHandle handle = Gdx.files.internal("assets/ui/uiskin.json");
    private static FileHandle fontHandle = Gdx.files.internal("assets/ui/fonts/roboto-medium.ttf");
    private static FileHandle nodeHandle = Gdx.files.internal("assets/ui/vpl/vplskin.atlas");

    public static FileHandle handle() { return handle; }
    public static void handle(FileHandle handle) { UIModule.handle = handle; }

    public static FileHandle nodeHandle() { return nodeHandle; }
    public static void nodeHandle(FileHandle nodeHandle) { UIModule.nodeHandle = nodeHandle; }

    @Override
    public void init() {
        VisUI.load(handle);
        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(fontHandle);
        FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();
        params.size = 12;
        BitmapFont font12 = gen.generateFont(params);
        VisUI.getSkin().add("default", font12, BitmapFont.class);
        gen.dispose();
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
