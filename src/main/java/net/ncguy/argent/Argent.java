package net.ncguy.argent;

import net.ncguy.argent.asset.ContentManager;
import net.ncguy.argent.core.VarRunnables;
import net.ncguy.argent.exception.ArgentInitialisationException;
import net.ncguy.argent.io.serialization.ISerializer;
import net.ncguy.argent.io.serialization.JSONSerializer;
import net.ncguy.argent.network.NetworkManager;
import net.ncguy.argent.physics.PhysicsCore;
import net.ncguy.argent.vpl.VPLCore;
import net.ncguy.argent.vr.OVRCore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guy on 08/06/2016.
 */
public class Argent {

    public static ContentManager content;
    public static ISerializer serial;
    public static VPLCore vpl;
    public static NetworkManager network;
    public static OVRCore vr;
    public static PhysicsCore physics;

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

    public static void initNetworkManager() { network = new NetworkManager(); }

    public static void initPhysics() { physics = new PhysicsCore(); }

    public static void initVR() {
        OVRCore vr;
        try {
            vr = new OVRCore();
        }catch (ArgentInitialisationException aie) {
            aie.printStackTrace();
            return;
        }
        Argent.vr = vr;
    }

    public static void initStandard() {
        initContentManager();
        initSerializer();
        initVPL();
        initNetworkManager();
        initPhysics();
    }

    public static boolean useHMD() {
        return Argent.vr != null;
    }


    public static List<VarRunnables.Var2Runnable<Integer>> onResize = new ArrayList<>();

    public static void draw(Runnable draw) {
        if(useHMD()) Argent.vr.renderToHMD(draw);
        else draw.run();
    }

    public static void resize(int width, int height) {
        onResize.forEach(r -> r.run(width, height));
    }

}
