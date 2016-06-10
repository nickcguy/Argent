package net.ncguy.argent;

import net.ncguy.argent.asset.ContentManager;
import net.ncguy.argent.io.serialization.ISerializer;
import net.ncguy.argent.io.serialization.JSONSerializer;
import net.ncguy.argent.vpl.VPLCore;

/**
 * Created by Guy on 08/06/2016.
 */
public class Argent {

    public static ContentManager content;
    public static ISerializer serial;
    public static VPLCore vpl;

    public static void initContentManager() {
        content = new ContentManager();
    }

    public static void initSerializer() {
        serial = JSONSerializer.instance();
    }

    public static void initVPL() { initVPL(""); }
    public static void initVPL(String nodePkg) {
        vpl = new VPLCore(nodePkg);
    }

    public static void initAll() {
        initContentManager();
        initSerializer();
        initVPL();
    }
}
