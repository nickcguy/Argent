package net.ncguy.argent.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Disposable;
import net.ncguy.argent.Argent;
import net.ncguy.argent.core.VarRunnables;
import net.ncguy.argent.render.renderer.DynamicRenderer;
import net.ncguy.argent.render.sample.UberRenderer;
import net.ncguy.argent.render.shader.DynamicShader;
import net.ncguy.argent.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

import static com.badlogic.gdx.Gdx.graphics;

/**
 * Created by Guy on 13/06/2016.
 */
public abstract class WorldRenderer<T> implements Disposable {

    public List<T> objects() { return objects; }

    protected List<T> objects;
    protected PerspectiveCamera camera;
    protected List<BufferRenderer<T>> renderPipe;
    protected BufferRenderer<T> finalBuffer;
    protected BufferRenderer currentRenderer = null;
    protected boolean canRender = true;

    protected VarRunnables.Var2Runnable<Integer> resizeRunnable;

    public WorldRenderer(List<T> objList) {
        this.objects = objList;
        this.renderPipe = new ArrayList<>();
        resizeRunnable = this::resize;
        Argent.onResize.add(resizeRunnable);
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

    public void clearRenderPipe() {
        renderPipe.clear();
        finalBuffer = null;
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

        int[] mutableId = new int[]{6};

        if(finalBuffer != null) {
//            buffer.getColorBufferTexture().bind(bufferId);
            if(finalBuffer.shaderProgram != null) {
                finalBuffer.shaderProgram.begin();
                renderPipe.forEach(b -> b.setSceneUniforms(finalBuffer.shaderProgram, mutableId));
                finalBuffer.shaderProgram.end();

                if(Gdx.input.isKeyJustPressed(Input.Keys.H)) {
                    System.out.println("Program Uniforms: ");
                    for(String s : finalBuffer.shaderProgram.getUniforms()) {
                        System.out.printf("\t%s: %s\n", s, finalBuffer.shaderProgram.getUniformLocation(s));
                    }
                }
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

    public Stack<BufferViewPack> bufferViews() {
        return bufferViews(false);
    }

    public Stack<BufferViewPack> bufferViews(boolean includeFinal) {
        final Stack<BufferViewPack> stack = new Stack<>();
        renderPipe.stream().filter(b -> b != finalBuffer).forEach(b -> {
            Sprite s = new Sprite(b.getBufferContents());
            s.setFlip(false, true);
            stack.push(new BufferViewPack(b.name(), s));
        });
        return stack;
    }

    public abstract ModelInstance getRenderable(T obj);
    public abstract void buildBulletCollision(T obj, btCollisionShape shape);
    public btRigidBody getBulletBody(T obj) { return null; }

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
        FileHandle vertHandle = Gdx.files.internal(String.format(shaderPathFormat, prefix, "vert"));
        FileHandle fragHandle = Gdx.files.internal(String.format(shaderPathFormat, prefix, "frag"));

        System.out.println(vertHandle.file().getAbsolutePath());
        System.out.println(fragHandle.file().getAbsolutePath());

        return new ShaderProgram(vertHandle, fragHandle);
    }

    @Override
    public void dispose() {
        Argent.onResize.remove(resizeRunnable);
        resizeRunnable = null;
    }

    public List<BufferRenderer<T>> pipe() {
        return renderPipe;
    }

    public List<DynamicRenderer<T>> dynamicPipe() {
        List<DynamicRenderer<T>> tmp = new ArrayList<>();
        pipe().stream().filter(b -> b instanceof DynamicRenderer).forEach(b -> tmp.add((DynamicRenderer<T>)b));
        if(finalBuffer instanceof DynamicRenderer) {
            if (!tmp.contains(finalBuffer))
                tmp.add((DynamicRenderer<T>) finalBuffer);
        }
        return tmp;
    }

    public void compileDynamicRenderPipe(List<DynamicShader.Info> renderInfo) {
        compileDynamicRenderPipe(renderInfo, true);
    }

    public void compileDynamicRenderPipe(List<DynamicShader.Info> renderInfo, boolean includeDefaults) {
        Stack<DynamicShader.Info> infoStack = CollectionUtils.listToStack(renderInfo);
        CollectionUtils.flipStack(infoStack);
        compileDynamicRenderPipe(infoStack, includeDefaults);
    }

    public void compileDynamicRenderPipe(Stack<DynamicShader.Info> infoStack, boolean includeDefaults) {
        clearRenderPipe();

        while(infoStack.size() > 1)
            this.addBufferRenderers(new DynamicRenderer<>(this, infoStack.pop()));
        if(includeDefaults) {
            this.addBufferRenderers(new UberRenderer<>(this));
        }
        DynamicShader.Info finalInfo = infoStack.pop();
        this.setFinalBuffer(new DynamicRenderer<>(this, finalInfo));
    }

    public static class BufferViewPack {
        public Sprite sprite;
        public String name;

        public BufferViewPack(String name, Sprite sprite) {
            this.name = name;
            this.sprite = sprite;
        }
    }

}
