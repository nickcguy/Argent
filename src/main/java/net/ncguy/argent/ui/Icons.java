package net.ncguy.argent.ui;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.VisUI;

import java.util.Arrays;

/**
 * Created by Guy on 22/07/2016.
 */
public class Icons {

    private static TextureAtlas icons, nodes;
    private static Skin iconSkin, nodeSkin;

    public static void init() {
        icons = new TextureAtlas("assets/icons/icons.atlas");
        nodes = new TextureAtlas(UIModule.nodeHandle());

        (iconSkin = new Skin()).addRegions(icons);
        (nodeSkin = new Skin()).addRegions(nodes);

        System.out.println("Node regions");
        nodes.getRegions().forEach(r -> {
            System.out.printf("\t%s: %s\n", r.name, Arrays.toString(r.splits));
        });
    }

    public static <T> T get(Skin skin, String name, Class<T> cls) {
        return skin.optional(name, cls);
    }

    public static TextureRegion getIconRegion (Skin skin, String name) {
        return skin.getRegion(name);
    }

    public static NinePatch getIconPatch(Skin skin, String name) {
        return skin.getPatch(name);
    }

    public interface IconSet {
        Drawable drawable();
        TextureRegion textureRegion();
        NinePatch patch();
        <T> T get(Class<T> cls);
    }

    public enum Icon implements IconSet {
        NEW("new"),
        UNDO("undo"),
        REDO("redo"),
        SETTINGS("settings"),
        SETTINGS_VIEW("settings-view"),
        EXPORT("export"),
        IMPORT("import"),
        LOAD("load"),
        SAVE("save"),
        GLOBE("globe"),
        INFO("info"),
        EXIT("exit"),
        FOLDER_OPEN("folder-open"),
        SEARCH("search"),
        MORE("more"),
        REFRESH("refresh-big"),

        WARNING("warning"),
        LAYER_ADD("layer-addZone"),
        LAYER_REMOVE("layer-remove"),
        LAYER_UP("layer-up"),
        LAYER_DOWN("layer-down"),
        EYE("eye"),
        EYE_DISABLED("eye-disabled"),
        LOCKED("locked"),
        UNLOCKED("unlocked"),
        ALIGN_LEFT("align-left"),
        ALIGN_RIGHT("align-right"),
        ALIGN_BOTTOM("align-bottom"),
        ALIGN_TOP("align-top"),
        ALIGN_CENTER_X("align-center-x"),
        ALIGN_CENTER_Y("align-center-y"),
        CURSOR("cursor"),
        POLYGON("polygon"),
        PLUS("plus"),
        PROGRESS("progress"),
        CHECK("check"),

        TOOL_MOVE("tool-move"),
        TOOL_ROTATE("tool-rotate"),
        TOOL_SCALE("tool-scale"),

        FOLDER_MEDIUM("folder-medium"),
        FOLDER_SOUND_MEDIUM("folder-sound-medium"),
        FOLDER_MUSIC_MEDIUM("folder-music-medium"),

        QUESTION_BIG("question-big"),
        SOUND_BIG("sound-big"),
        MUSIC_BIG("music-big"),
        POINT_BIG("point-big"),
        PARTICLE_BIG("particle-big"),

        FOLDER_NEW("icon-folder-new", true),
        FOLDER_PARENT("icon-folder-parent", true),
        CLOSE("icon-close", true),
        FOLDER("icon-folder", true),
        ARROW_LEFT("icon-arrow-left", true),
        ARROW_RIGHT("icon-arrow-right", true);
        ;

        public final String name;
        private final boolean fromVisUI;

        Icon (String name) {
            this.name = name;
            fromVisUI = false;
        }

        Icon (String name, boolean fromVisUI) {
            this.name = name;
            this.fromVisUI = fromVisUI;
        }

        public Drawable drawable () {
            if (fromVisUI)
                return VisUI.getSkin().getDrawable(name);
            else
                return new TextureRegionDrawable(textureRegion());
        }

        public TextureRegion textureRegion () {
            if (fromVisUI)
                return VisUI.getSkin().getRegion(name);
            else
                return Icons.getIconRegion(iconSkin, this.name);
        }

        public NinePatch patch() {
            return Icons.getIconPatch(iconSkin, this.name);
        }

        public <T> T get(Class<T> cls) {
            return Icons.get(iconSkin, this.name, cls);
        }

    }

    public enum Node implements IconSet {
        NODE_BODY("Node_Body"),
        NODE_SELECTED("Node_Selected"),
        NODE_TITLE("Node_Title"),
        NODE_TITLE_GLOSS("Node_Title_Gloss"),
        NODE_TITLE_HIGHLIGHT("Node_Title_Highlight"),
        PIN_CONNECTED("Pin_Connected"),
        PIN("Pin_Disconnected"),
        PIN_EXEC_CONNECTED("ExecPin_Connected"),
        PIN_EXEC("ExecPin_Disconnected"),
        NODE_INVALIDATE("Node_Invalidate"),
        ASSET_BG("AssetBackground"),
        HAZARD_TAPE("HazardTape"),
        ARROW("Arrow"),
        ARROW_HOVER("Arrow_Hover"),
        TABLEHEADER("TableViewHeader"),
        ASSET_BG_SELECTED("AssetBackground_sel"),
        DROP_ZONE("DropZoneIndicator"),
        ;
        Node(String name) {
            this.name = name;
        }
        private String name;

        public Drawable drawable () {
                return new TextureRegionDrawable(textureRegion());
        }

        public TextureRegion textureRegion () {
            return Icons.getIconRegion(nodeSkin, this.name);
        }

        public NinePatch patch() {
            return Icons.getIconPatch(nodeSkin, this.name);
        }

        public <T> T get(Class<T> cls) {
            return Icons.get(nodeSkin, this.name, cls);
        }

    }

}
