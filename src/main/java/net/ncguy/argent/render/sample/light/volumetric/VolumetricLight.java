package net.ncguy.argent.render.sample.light.volumetric;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import net.ncguy.argent.render.WorldRenderer;
import net.ncguy.argent.utils.ScreenshotFactory;

/**
 * Created by Guy on 12/07/2016.
 */
public abstract class VolumetricLight {

    protected WorldRenderer world;
    protected Quaternion lightData;
    public static int SIZE = 1024;

    public VolumetricLight(WorldRenderer world) {
        this.world = world;
        this.lightData = new Quaternion();
    }

    public VolumetricLight(WorldRenderer world, Quaternion lightData) {
        this.world = world;
        this.lightData = lightData;
    }

    public VolumetricLight(WorldRenderer world, float x, float y, float z, float w) {
        this(world, new Quaternion(x, y, z, w));
    }

    public void setLight(float x, float y, float z, float intensity) {
        this.lightData.set(x, y, z, intensity);
    }

    public void setLight(Vector3 pos, float intensity) {
        setLight(pos.x, pos.y, pos.z, intensity);
    }

    protected Camera camera;
    public Camera camera() {
        if(camera == null) {
            camera = new PerspectiveCamera(120, SIZE, SIZE);
            camera.position.set(lightData.x, lightData.y, lightData.z);
            camera.near = 0.01f;
            camera.far = lightData.w;
            camera.lookAt(0, 0, 0);
        }
        return camera;
    }
    protected FrameBuffer fbo;
    public FrameBuffer fbo() {
        if(fbo == null) {
            fbo = new FrameBuffer(Pixmap.Format.RGBA8888, SIZE, SIZE, true);
        }
        return fbo;
    }

    public void update() {
        camera().position.set(lightData.x, lightData.y, lightData.z);
        camera().up.set(0, 1, 0);
        camera().update(true);
    }

    public void render(ModelBatch batch) {
        update();
        fbo().begin();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        batch.begin(camera());
        batch.render(world.renderables());
        batch.end();
        if(Gdx.input.isKeyJustPressed(Input.Keys.F2))
            ScreenshotFactory.saveScreenshot(fbo().getWidth(), fbo().getHeight(), "Light");
        fbo().end();
    }

    public abstract void applyToShader(ShaderProgram program);

    public float[] pack() {
        return new float[]{
            lightData.x,
            lightData.y,
            lightData.z,
            lightData.w
        };
    }
}
