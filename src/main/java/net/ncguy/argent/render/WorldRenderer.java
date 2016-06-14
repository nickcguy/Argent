package net.ncguy.argent.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

import static com.badlogic.gdx.Gdx.graphics;

/**
 * Created by Guy on 13/06/2016.
 */
public abstract class WorldRenderer<T> {

    protected List<T> objects;
    protected PerspectiveCamera camera;
    protected List<BufferRenderer<T>> renderPipe;
    protected BufferRenderer<T> finalBuffer;
    protected BufferRenderer currentRenderer = null;
    protected boolean canRender = true;

    public WorldRenderer(List<T> objList) {
        this.objects = objList;
        this.renderPipe = new ArrayList<>();
    }

    public PerspectiveCamera camera() {
        if(camera == null) {
            camera = new PerspectiveCamera(90, graphics.getWidth(), graphics.getHeight());
            camera.near = .1f;
            camera.far = 1000;
            camera.position.set(10, 0, 0);
            camera.lookAt(0, 0, 0);
        }
        camera.update();
        return camera;
    }

    @SafeVarargs
    public final void addBufferRenderers(BufferRenderer<T>... renderers) {
        for (BufferRenderer<T> renderer : renderers) {
            if(!renderPipe.contains(renderer))
                renderPipe.add(renderer);
        }
    }

    public void invalidateFBOs() {
        renderPipe.forEach(BufferRenderer::invalidateFBO);
    }

    public void setFinalBuffer(BufferRenderer<T> renderer) {
        if(renderPipe.contains(renderer)) renderPipe.remove(renderer);
        this.finalBuffer = renderer;
    }

    public void render(final float delta) {
        if(!canRender) return;
        canRender = false;
        for (BufferRenderer<T> b : renderPipe) {
            currentRenderer = b;
            b.render(delta, false);
        }
//        FrameBuffer buffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()*renderPipe.size(), true);
//        SpriteBatch bufferBatch = new SpriteBatch();
//        buffer.begin();
//        bufferBatch.begin();
//        int index = 0;
//        for (BufferRenderer<T> b : renderPipe)
//            bufferBatch.draw(b.fbo().getColorBufferTexture(), 0, Gdx.graphics.getHeight()*(++index));
//        bufferBatch.end();
//        buffer.end();


        int[] mutableId = new int[]{6};

        if(finalBuffer != null) {
//            buffer.getColorBufferTexture().bind(bufferId);
            if(finalBuffer.shaderProgram != null) {
                finalBuffer.shaderProgram.begin();
                renderPipe.forEach(b -> b.setSceneUniforms(finalBuffer.shaderProgram, mutableId));
                finalBuffer.shaderProgram.setUniformf("u_screenRes", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                finalBuffer.shaderProgram.setUniformf("u_cameraDir", camera().direction);
                finalBuffer.shaderProgram.setUniformf("u_cameraPos", camera().position);
                finalBuffer.shaderProgram.end();
            }

            currentRenderer = finalBuffer;
            finalBuffer.render(delta);
        }
//        buffer.dispose();
        currentRenderer = null;
        canRender = true;
    }

    public void resize(int width, int height) {
        this.invalidateFBOs();
        camera().viewportWidth = width;
        camera().viewportHeight = height;
        camera().update(true);
    }

    public List<ModelInstance> renderables() {
        final List<ModelInstance> instances = new ArrayList<>();
        this.objects.forEach(obj -> {
            Optional<ModelInstance> inst = getRenderableOptional(obj);
            if(inst.isPresent())
                instances.add(inst.get());
        });
        return instances;
    }

    public Stack<Sprite> bufferViews() {
        return bufferViews(false);
    }

    public Stack<Sprite> bufferViews(boolean includeFinal) {
        final Stack<Sprite> stack = new Stack<>();
        renderPipe.stream().filter(b -> b != finalBuffer).forEach(b -> {
            Sprite s = new Sprite(b.getBufferContents());
            s.setFlip(false, true);
            stack.push(s);
        });
        return stack;
    }

    public abstract ModelInstance getRenderable(T obj);

    public Optional<ModelInstance> getRenderableOptional(T obj) {
        ModelInstance inst = getRenderable(obj);
        if(inst != null)
            return Optional.of(inst);
        return Optional.empty();
    }

    public BufferRenderer currentRenderer() { return currentRenderer; }

    // STATIC

    /**
     * The internal path to the shader files. 2 arguments are required to identify, and must be as follows
     * 1: the shader prefix
     * 2: frag or vert
     */
    public static String shaderPathFormat = "shaders/%s/%s.glsl";

    public static ShaderProgram setupShader(String prefix) {
        ShaderProgram.pedantic = false;
        return new ShaderProgram(Gdx.files.internal(String.format(shaderPathFormat, prefix, "vert")), Gdx.files.internal(String.format(shaderPathFormat, prefix, "frag")));
    }

}
