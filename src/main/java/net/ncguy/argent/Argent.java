package net.ncguy.argent;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import net.ncguy.argent.asset.ContentManager;
import net.ncguy.argent.core.VarRunnables;
import net.ncguy.argent.exception.ArgentInitialisationException;
import net.ncguy.argent.io.serialization.ISerializer;
import net.ncguy.argent.io.serialization.JSONSerializer;
import net.ncguy.argent.network.NetworkManager;
import net.ncguy.argent.parser.IParser;
import net.ncguy.argent.physics.PhysicsCore;
import net.ncguy.argent.ui.NotificationActor;
import net.ncguy.argent.ui.NotificationContainer;
import net.ncguy.argent.vpl.VPLCore;
import net.ncguy.argent.vr.OVRCore;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
        stage();
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

    public static void log(String text, boolean dialog) {
        log("", text, dialog);
    }
    public static void log(String title, String text, boolean dialog) {
        log(title, text, dialog ? 3 : -1);
    }
    public static void log(String title, String text, int dialogLen) {
        System.out.println(title+": "+text);
        if(dialogLen > 0)
            new NotificationActor(title, text, VisUI.getSkin(), dialogLen).addToStage(container, new Vector2(notifiationAnchorX, notifiationAnchorY)).open();
    }


    public static <T> void toast(String title, String text, IParser<T> parser) {
        toast(title, text, parser, 3);
    }
    public static <T> void toast(String title, String text, IParser<T> parser, int duration) {
        Set<T> set = parser.parse(text);
        set.forEach(s -> log(title, s.toString(), duration));
    }

    public static void setNotificationAnchor(float x, float y) {
        notifiationAnchorX = MathUtils.clamp(x, 0.0f, 1.0f);
        notifiationAnchorY = MathUtils.clamp(y, 0.0f, 1.0f);
    }

    private static float notifiationAnchorX = 1;
    private static float notifiationAnchorY = 1;

    private static Stage stage;
    private static NotificationContainer container;
    private static Stage stage() {
        if (stage == null) {
            stage = new Stage(new ScreenViewport(new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight())));
            container = new NotificationContainer();
            container.setSize(256, Gdx.graphics.getHeight());
            container.setPosition(Gdx.graphics.getWidth()-256, 0);
            Argent.onResize.add((w, h) -> {
                stage().getViewport().update(w, h, true);
                container.setSize(256, h);
                container.setPosition(w-256, 0);
            });
            stage.addActor(container);
        }
        return stage;
    }

    public static void render(float delta) {
        stage().act(delta);
        stage().draw();
    }

    public static class GlobalConfig {

        public static float exposure = 1.0f;

    }

}

