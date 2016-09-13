package net.ncguy.argent.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.kotcrab.vis.ui.VisUI;
import net.ncguy.argent.IModule;
import net.ncguy.argent.content.ContentModule;
import net.ncguy.argent.injector.InjectionStore;

/**
 * Created by Guy on 17/07/2016.
 */
public class UIModule extends IModule {

//    private static FileHandle handle = VisUI.SkinScale.X1.getSkinFile();
    private static FileHandle handle = Gdx.files.internal("assets/ui/uiskin.json");
    private static FileHandle fontHandle = Gdx.files.internal("assets/ui/fonts/roboto-medium.ttf");
    private static FileHandle nodeHandle = Gdx.files.internal("assets/ui/vpl/vplskin.atlas");

    private static FileHandle borderHandle = Gdx.files.internal("assets/ui/tiledBorder/tiledBorder.atlas");

    public static FileHandle handle() { return handle; }
    public static void handle(FileHandle handle) { UIModule.handle = handle; }

    public static FileHandle nodeHandle() { return nodeHandle; }
    public static void nodeHandle(FileHandle nodeHandle) { UIModule.nodeHandle = nodeHandle; }

    @Override
    public void init() {
        VisUI.load(handle);
        Icons.init();
        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(fontHandle);
        FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();
        params.size = 16;
        BitmapFont font16 = gen.generateFont(params);
        params.size = 12;
        BitmapFont font12 = gen.generateFont(params);

//        Label.LabelStyle style = new Label.LabelStyle();
//        style.font = font12;
//        style.fontColor = Color.WHITE;
//        VisUI.getSkin().add("small", style, Label.LabelStyle.class);

        VisUI.getSkin().get("default", Label.LabelStyle.class).font = font16;
        VisUI.getSkin().get("small", Label.LabelStyle.class).font = font12;

        TextureAtlas borderAtlas = new TextureAtlas(borderHandle);
        VisUI.getSkin().addRegions(borderAtlas);

        gen.dispose();

        DragAndDrop dnd = new DragAndDrop();
        try {
            InjectionStore.setGlobal(dnd);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
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
