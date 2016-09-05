package net.ncguy.argent;

import com.badlogic.gdx.math.MathUtils;
import net.ncguy.argent.render.argent.ArgentRenderer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guy on 27/08/2016.
 */
public class GlobalSettings {

    public static boolean binaryFileSizes = true;
    /**
     * Enable use of the geometry shader, currently does not work with dynamic shaders
     */
    public static boolean useGeometryShader = false;

    private static List<String> boolVars = new ArrayList<>();

    public static void addBoolVar(String key) {
        boolVars.add(key);
    }

    public static void removeBoolVar(String key) {
        boolVars.remove(key);
    }

    public static void toggleBoolVar(String key) {
        if(hasBoolVar(key)) removeBoolVar(key);
        else addBoolVar(key);
    }

    public static boolean hasBoolVar(String key) {
        return boolVars.contains(key);
    }

    public static class VarKeys {
        public static final String bool_LIGHTDEBUG = "light.debug";
        public static final String bool_SHADOWS = "light.shadow";
    }

    private static int rendererIndex = 1;
    public static int rendererIndex() {
        return rendererIndex;
    }

    public static void rendererIndex(int rendererIndex) {
        rendererIndex = MathUtils.clamp(rendererIndex, 0, ArgentRenderer.ltg_ATTACHMENTS.length-1);
        GlobalSettings.rendererIndex = rendererIndex;
    }
}
