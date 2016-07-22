package net.ncguy.argent.ui;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.VisUI;

/**
 * Created by Guy on 22/07/2016.
 */
public class Icons {

    private static TextureAtlas icons;

    public static void init() {
        icons = new TextureAtlas("assets/icons/icons.atlas");
    }

    public static Drawable getIcon (Icon name) {
        return new TextureRegionDrawable(getIconRegion(name));
    }

    public static TextureRegion getIconRegion (Icon icon) {
        return icons.findRegion(icon.name);
    }

    public enum Icon {
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

        WARNING("warning"),
        LAYER_ADD("layer-add"),
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
                return Icons.getIconRegion(this);
        }

    }

}
