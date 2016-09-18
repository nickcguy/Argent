package net.ncguy.argent.misc.shader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;
import net.ncguy.argent.Argent;
import net.ncguy.argent.utils.AppUtils;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by Guy on 18/09/2016.
 */
public class Shaders implements Disposable {

    public WireframeShader wireframeShader;
    public SolidShader solidShader;
    public MaskedShader maskedShader;

    private Consumer<Integer> keyDown;
    private BiConsumer<Integer, Integer> invalidateFBO;
    public FrameBuffer fbo;

    public Supplier<Texture> depthFBOSupplier;
    public PickerShader pickerShader;

    private Shaders() {
        Shaders.instance = this;
        this.keyDown = this::onKeyDown;
        this.invalidateFBO = this::invalidateFBO;
        Argent.addOnKeyDown(keyDown);
        Argent.addOnResize(this.invalidateFBO);
        init();
    }

    private void onKeyDown(int code) {
        if(code == Input.Keys.O && AppUtils.Input.isShiftPressed()) {
            disposeShaders();
            initShaders();
        }
    }

    private void initShaders() {
        ShaderProgram.pedantic = false;
        wireframeShader = new WireframeShader();
        wireframeShader.init();
        solidShader = new SolidShader();
        solidShader.init();
        maskedShader = new MaskedShader();
        maskedShader.init();
        pickerShader = new PickerShader();
        pickerShader.init();
    }

    private void disposeShaders() {
        wireframeShader.dispose();
        solidShader.dispose();
        maskedShader.dispose();
        pickerShader.dispose();
    }

    private void init() {
        initShaders();
    }

    @Override
    public void dispose() {
        disposeShaders();
        Argent.removeOnKeyDown(this.keyDown);
        Argent.removeOnResize(this.invalidateFBO);
    }

    private static Shaders instance;
    public static Shaders instance() {
        if(instance == null) return new Shaders();
        return instance;
    }

    public void invalidateFBO(int w, int h) {
        if(fbo != null) {
            fbo.dispose();
            fbo = null;
        }
    }

    public FrameBuffer fbo() {
        if(fbo == null) {
            fbo = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        }
        return fbo;
    }
}
