package net.ncguy.argent;

import net.ncguy.argent.asset.ContentManager;
import net.ncguy.argent.io.serialization.ISerializer;
import net.ncguy.argent.io.serialization.JSONSerializer;
import net.ncguy.argent.vpl.VPLCore;

/**
 * Created by Guy on 08/06/2016.
 */
public class Argent {

    public static ContentManager content = ContentManager.instance();
    public static ISerializer serial = JSONSerializer.instance();
    public static VPLCore vpl = new VPLCore("");

    public static void init() {
    }

}
