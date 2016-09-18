package net.ncguy.argent.render.argent;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import net.ncguy.argent.Argent;
import net.ncguy.argent.GlobalSettings;
import net.ncguy.argent.assets.ArgentShaderProvider;
import net.ncguy.argent.diagnostics.DiagnosticsModule;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.entity.components.ArgentComponent;
import net.ncguy.argent.entity.components.light.DirLightComponent;
import net.ncguy.argent.entity.components.light.LightComponent;
import net.ncguy.argent.entity.components.light.PointLightComponent;
import net.ncguy.argent.entity.components.light.SpotLightComponent;
import net.ncguy.argent.event.StringPacketEvent;
import net.ncguy.argent.event.shader.RefreshFBOEvent;
import net.ncguy.argent.render.BasicWorldRenderer;
import net.ncguy.argent.utils.AppUtils;
import net.ncguy.argent.utils.MultiTargetFrameBuffer;
import net.ncguy.argent.utils.ScreenshotUtils;
import net.ncguy.argent.world.GameWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Guy on 23/07/2016.
 */
public class ArgentRenderer<T extends WorldEntity> extends BasicWorldRenderer<T> implements RefreshFBOEvent.RefreshFBOListener {

    MultiTargetFrameBuffer textureMRT;
    ModelBatch textureBatch, mutableTextureBatch;
    ShaderProgram textureProgram;

    MultiTargetFrameBuffer lightingMRT;
    ModelBatch lightingBatch;
    ShaderProgram lightingProgram;

    MultiTargetFrameBuffer quadFBO;
    ModelBatch quadBatch;
    ShaderProgram quadProgram;

    ShaderProgram screenShader;
    SpriteBatch screenBatch;

    Vector2 size = new Vector2();
    int dirLightCount = 0;
    int pointLightCount = 0;
    int spotLightCount = 0;
//    Sprite screenSprite;

    public static final FBOAttachment tex_NORMAL = new FBOAttachment(0, "texNormal");
    public static final FBOAttachment tex_DIFFUSE = new FBOAttachment(1, "texDiffuse");
    public static final FBOAttachment tex_SPCAMBDIS = new FBOAttachment(2, "texSpcAmbDisRef");
    public static final FBOAttachment tex_EMISSIVE = new FBOAttachment(3, "texEmissive");
    public static final FBOAttachment tex_POSITION = new FBOAttachment(4, "texPosition");
    public static final FBOAttachment tex_MODIFIEDNORMAL = new FBOAttachment(5, "texModNormal");

    public static final FBOAttachment[] tex_ATTACHMENTS = new FBOAttachment[]{
        tex_NORMAL, tex_DIFFUSE, tex_SPCAMBDIS, tex_EMISSIVE, tex_POSITION, tex_MODIFIEDNORMAL
    };

    public static final FBOAttachment ltg_POSITION = new FBOAttachment(0, "ltgPosition");
    public static final FBOAttachment ltg_TEXTURES = new FBOAttachment(1, "ltgTextures");
    public static final FBOAttachment ltg_LIGHTING = new FBOAttachment(2, "ltgLighting");
    public static final FBOAttachment ltg_GEOMETRY = new FBOAttachment(3, "ltgGeometry");
    public static final FBOAttachment ltg_REFLECTION = new FBOAttachment(4, "ltgReflection");
    public static final FBOAttachment ltg_EMISSIVE = new FBOAttachment(5, "ltgEmissive");


    public static final FBOAttachment[] ltg_ATTACHMENTS = new FBOAttachment[] {
        ltg_POSITION, ltg_TEXTURES, ltg_LIGHTING, ltg_GEOMETRY, ltg_REFLECTION, ltg_EMISSIVE
    };

    public static final int previousFrameIndex = 8;

    private DepthMapRenderer<T> depthMapRenderer;

    public ArgentRenderer(GameWorld<T> world) {
        this(world, true);
    }

    public ArgentRenderer(GameWorld<T> world, boolean attachGlobalListeners) {
        super(world);
        Argent.event.register(this);
        size.set(camera().viewportWidth, camera().viewportHeight);
        depthMapRenderer = new DepthMapRenderer<>(world);
        refreshShaders();
        refreshFBO();
//        Argent.addOnResize(this::resize);

        if(attachGlobalListeners) attachGlobalListeners();
    }

    public void attachGlobalListeners() {
        Argent.addOnResize(this::argentResize);
        Argent.addOnKeyDown(key -> {
            if(key == Input.Keys.O)
                if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
                    refreshShaders();
        });
    }

    public void removeGlobalListeners() {

    }

    @Override
    public ModelBatch batch() {
        if(modelBatch == null) {
            if(GlobalSettings.useGeometryShader) screenShader = AppUtils.Shader.loadGeometryShader("pipeline/screen");
            else screenShader = AppUtils.Shader.loadShader("pipeline/screen");
            modelBatch = new ModelBatch(new DefaultShaderProvider() {
                @Override
                protected Shader createShader(Renderable renderable) {
                    return new ArgentScreenShader(renderable, screenShader, null);
                }
            });
        }
        return modelBatch;
    }

    public void argentResize(int width, int height) {
        size.set(width, height);
        camera().viewportWidth = width;
        camera().viewportHeight = height;
        camera().update(true);
        if(width <= 0 || height <= 0) return;
        refreshFBO();
    }

    @Override
    public void resize(int width, int height) {
        if(width == 0 || height == 0) return;
        if(camera().viewportWidth == width && camera().viewportHeight == height) return;
        super.resize(width, height);
//        System.out.printf("[%s, %s]\n", width, height);
//        refreshShaders();
    }

    @Override
    public void render(ModelBatch batch, float delta) {
        depthMapRenderer.render(batch, delta);
        renderTexture(delta);
        applyGBufferToLighting();
        renderLighting(delta);
        applyLightingToQuad();
        renderToQuad(delta);
        applyToScreen();
        renderToScreen(delta);
    }

    private void renderToMRT(MultiTargetFrameBuffer mrt, ModelBatch batch, float delta) {
        renderToMRT(mrt, batch, delta, false);
    }

    private void renderToMRT(MultiTargetFrameBuffer mrt, ModelBatch batch, float delta, boolean useComponents) {
        mrt.begin();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
        super.render(batch, delta);
        if(Gdx.input.isKeyJustPressed(Input.Keys.F2))
            ScreenshotUtils.saveScreenshot(iWidth(), iHeight(), "Batch");
        mrt.end();
    }

    public void renderTexture(float delta) {
        renderToMRT(textureMRT, mutableTextureBatch, delta, true);
    }
    public void renderLighting(float delta) {
        renderToMRT(lightingMRT, lightingBatch, delta);
    }
    public void renderToQuad(float delta) {
        quadFBO.begin();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
        super.render(quadBatch, delta);
        quadFBO.end();
    }

    public void applyToScreen() {
        screenShader.begin();
        int id = 1;
        quadFBO.getColorBufferTexture(0).bind(id);
        screenShader.setUniformi("u_quadBuffer", id);
        screenShader.end();
    }

    private Mesh mesh;
    public Mesh mesh() {
        if (mesh == null) {
            float[] verts = new float[20];
            int i = 0;
            verts[i++] = -1f;
            verts[i++] = -1;
            verts[i++] = 0;
            verts[i++] = 0;
            verts[i++] = 0;

            verts[i++] = 1;
            verts[i++] = -1;
            verts[i++] = 0;
            verts[i++] = 1;
            verts[i++] = 0;

            verts[i++] = 1;
            verts[i++] = 1;
            verts[i++] = 0;
            verts[i++] = 1;
            verts[i++] = 1;

            verts[i++] = -1f;
            verts[i++] = 1;
            verts[i++] = 0;
            verts[i++] = 0;
            verts[i++] = 1;

            mesh = new Mesh(true, 4, 0,
                    new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
                    new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE+"0"));
            mesh.setVertices(verts);
        }
        return mesh;
    }

    public void renderToScreen(float delta) {
        // TODO render to quad before displaying on screen
        super.render(batch(), delta);
    }

    public void applyGBufferToLighting() {
        List<LightComponent>[] lightingData = fetchLightingData();
        lightingProgram.begin();
        bindLightingData(lightingProgram, lightingData);
        bindToMRT(textureMRT, lightingProgram, tex_ATTACHMENTS);
        bindPreviousFrame(lightingProgram);
        lightingProgram.end();
    }

    public void bindPreviousFrame(ShaderProgram program) {
        bindPreviousFrame(program, previousFrameIndex);
    }

    public void bindPreviousFrame(ShaderProgram shaderProgram, int index) {
        quadFBO.getColorBufferTexture(1).bind(index);
        shaderProgram.setUniformi("u_previousFrame", index);
    }

    public List<LightComponent>[] fetchLightingData() {
        final List<LightComponent> lights = new ArrayList<>();
        world.instances().forEach(i -> i.components().stream()
                .filter(this::bindLightData_Filter)
                .map(this::bindLightData_Map)
                .forEach(lights::add));
        List<LightComponent> dirLights = lights.stream().filter(l -> l instanceof DirLightComponent).collect(Collectors.toList());
        List<LightComponent> pointLights = lights.stream().filter(l -> l instanceof PointLightComponent).collect(Collectors.toList());
        List<LightComponent> spotLights = lights.stream().filter(l -> l instanceof SpotLightComponent).collect(Collectors.toList());

        boolean recompile = false;

        if(dirLights.size() != dirLightCount) {
            dirLightCount = dirLights.size();
            recompile = true;
        }
        if(pointLights.size() != pointLightCount) {
            pointLightCount = pointLights.size();
            recompile = true;
        }if(spotLights.size() != spotLightCount) {
            spotLightCount = spotLights.size();
            recompile = true;
        }
        if(recompile) refreshLightingShader();

        if(Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            if (!Argent.isModuleLoaded(DiagnosticsModule.class))
                Argent.loadModule(new DiagnosticsModule());
            Argent.diagnostics.invokeShaderViewer(viewer -> viewer.show("Lighting", lightingProgram.getVertexShaderSource(), lightingProgram.getFragmentShaderSource()));
        }

        return new List[]{dirLights, pointLights, spotLights};
    }

    public void bindLightingData(ShaderProgram shaderProgram, List<LightComponent>[] lights) {
        final int[] i = new int[]{0};
        lights[0].forEach(l -> {
            String prefix = "directionalLights["+i[0]+"]";
            l.bind(shaderProgram, prefix);
            i[0]++;
        });
        i[0] = 0;
        lights[1].forEach(l -> {
            String prefix = "pointLights["+i[0]+"]";
            l.bind(shaderProgram, prefix);
            i[0]++;
        });
        i[0] = 0;
        lights[2].forEach(l -> {
            String prefix = "spotLights["+i[0]+"]";
            l.bind(shaderProgram, prefix);
            i[0]++;
        });
    }

    private String getLightArr(LightComponent light) {
        if(light instanceof DirLightComponent) return "directionalLights";
        if(light instanceof PointLightComponent) return "pointLights";
        if(light instanceof SpotLightComponent) return "spotLights";
        return "lights";
    }

    private boolean bindLightData_Filter(ArgentComponent c) {
        return c instanceof LightComponent;
    }
    private LightComponent bindLightData_Map(ArgentComponent c) {
        return (LightComponent)c;
    }

    public void applyLightingToQuad() {
        Texture emissive = generateBlurredEmissive();
        quadProgram.begin();
        bindToMRT(lightingMRT, quadProgram, 0, ltg_ATTACHMENTS);
        applyFinalTextureToQuad(ltg_ATTACHMENTS[GlobalSettings.rendererIndex()], 0);
        emissive.bind(ltg_EMISSIVE.id);
        quadProgram.end();
    }

    private void applyFinalTextureToQuad(FBOAttachment attachment, int offset) {
        quadProgram.setUniformi("ltgFinalColour", attachment.id + offset);
    }

    private void bindToMRT(MultiTargetFrameBuffer mrt, ShaderProgram shader, FBOAttachment... attachments) {
        bindToMRT(mrt, shader, 0, attachments);
    }

    private void bindToMRT(MultiTargetFrameBuffer mrt, ShaderProgram shader, int offset, FBOAttachment... attachments) {
        for (FBOAttachment attachment : attachments) {
            int id = attachment.id + offset;
            Texture tex = mrt.getColorBufferTexture(attachment.id);
            if(tex != null) tex.bind(id);
            attachment.bind(shader, id);
        }
//        mrt.getColorBufferTexture(tex_DIFFUSE.id).bind(tex_DIFFUSE.id);
//        shader.setUniformi(tex_DIFFUSE.name, tex_DIFFUSE.id);
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
    }

    public MultiTargetFrameBuffer getTextureMRT()  { return textureMRT;  }
    public MultiTargetFrameBuffer getLightingMRT() { return lightingMRT; }
    public MultiTargetFrameBuffer getQuadFBO() { return quadFBO; }

    @Override
    public MultiTargetFrameBuffer[] getMrts() {
        return new MultiTargetFrameBuffer[] { textureMRT, lightingMRT, quadFBO, blurMRT};
    }

    @Override
    public String[] getMrtNames() {
        String[] texMrt = AppUtils.General.extract(tex_ATTACHMENTS, String.class, a -> a.name);
        String[] ltgMrt = AppUtils.General.extract(ltg_ATTACHMENTS, String.class, a -> a.name);
        String[] quadMrt = new String[] { "Quad", "Quad Store", "Quad Blur" };
        String[] blurMrt = new String[] { "Blur 1" };
        return AppUtils.General.union(String.class, texMrt, ltgMrt, quadMrt, blurMrt);
    }

    public void refreshFBO() {
        if(textureMRT != null) {
            textureMRT.dispose();
            textureMRT = null;
        }
        if(lightingMRT != null) {
            lightingMRT.dispose();
            lightingMRT = null;
        }
        if(quadFBO != null) {
            quadFBO.dispose();
            quadFBO = null;
        }
        if(blurMRT != null) {
            blurMRT.dispose();
            blurMRT = null;
        }

        textureMRT = create(tex_ATTACHMENTS.length, Pixmap.Format.RGBA8888, GlobalSettings.ResolutionScale.texture());
        lightingMRT = create(ltg_ATTACHMENTS.length, MultiTargetFrameBuffer.Format.RGB32F, GlobalSettings.ResolutionScale.lighting());
        quadFBO = create(3, Pixmap.Format.RGBA8888, GlobalSettings.ResolutionScale.quad());
        blurMRT = create(1, Pixmap.Format.RGB565, GlobalSettings.ResolutionScale.blur());

        refreshBlurShader();
//        DebugPreview debugPreview = DebugPreview.instance();
//        if(debugPreview != null) debugPreview.build(textureMRT, lightingMRT, quadFBO, blurMRT);
    }

    public void refreshShaders() {
        new StringPacketEvent("toast|info", "Reloading Shaders").fire();
        ShaderProgram.pedantic = false;
        refreshTextureShader();
        refreshLightingShader();
        refreshQuadShader();
        refreshScreenShader();
        refreshBlurShader();

        depthMapRenderer.invalidateBatch();
    }

    public void refreshTextureShader() {
        if(textureProgram != null) {
            textureProgram.dispose();
            textureProgram = null;
        }
        if(textureBatch != null) {
            textureBatch.dispose();
            textureBatch = null;
        }
        if(mutableTextureBatch != null) {
            mutableTextureBatch.dispose();
            mutableTextureBatch = null;
        }
        if(GlobalSettings.useGeometryShader) textureProgram = AppUtils.Shader.loadGeometryShader("pipeline/texture");
        else textureProgram = AppUtils.Shader.loadShader("pipeline/texture");
        textureBatch = new ModelBatch(new DefaultShaderProvider() {
            @Override
            protected Shader createShader(Renderable renderable) {
                return new SmartTextureShader(renderable, textureProgram);
            }
        });
        mutableTextureBatch = new ModelBatch(new ArgentShaderProvider());
    }
    public void refreshLightingShader() {
        if(lightingProgram != null) {
            lightingProgram.dispose();
            lightingProgram = null;
        }
        if(lightingBatch != null) {
            lightingBatch.dispose();
            lightingBatch = null;
        }
        if(GlobalSettings.useGeometryShader) lightingProgram = AppUtils.Shader.loadGeometryShader("pipeline/lighting");
        else lightingProgram = AppUtils.Shader.loadShaderWithPrefix("pipeline/lighting", () -> "", () -> {
            String s = "#version 450\n";
            if(dirLightCount > 0) s += "#define NR_DIRECTIONAL "+dirLightCount+"\n";
            if(pointLightCount > 0) s += "#define NR_POINTLIGHTS "+pointLightCount+"\n";
            if(spotLightCount > 0) s += "#define NR_SPOTLIGHTS "+spotLightCount+"\n";
            s += "/*\n";
            return s;
        });
        lightingBatch = new ModelBatch(new DefaultShaderProvider() {
            @Override
            protected Shader createShader(Renderable renderable) {
                return new ArgentLightShader(renderable, lightingProgram, vec -> vec.set(lightingMRT.getWidth(), lightingMRT.getHeight()));
            }
        });
    }
    public void refreshQuadShader() {
        if(quadProgram != null) {
            quadProgram.dispose();
            quadProgram = null;
        }
        if(quadBatch != null) {
            quadBatch.dispose();
            quadBatch = null;
        }
        if(GlobalSettings.useGeometryShader) quadProgram = AppUtils.Shader.loadGeometryShader("pipeline/quad");
        else quadProgram = AppUtils.Shader.loadShader("pipeline/quad");
        quadBatch = new ModelBatch(new DefaultShaderProvider() {
            @Override
            protected Shader createShader(Renderable renderable) {
                return new ArgentQuadShader(renderable, quadProgram, vec -> vec.set(quadFBO.getWidth(), quadFBO.getHeight()));
            }
        });
    }
    public void refreshScreenShader() {
        if(screenShader != null) {
            screenShader.dispose();
            screenShader = null;
        }
        if(screenBatch != null) {
            screenBatch.dispose();
            screenBatch = null;
        }
        screenShader = AppUtils.Shader.loadShader("pipeline/screen");
        screenBatch = new SpriteBatch(1024, screenShader);
    }

    public void refreshBlurShader() {
        if(blurProgram != null) {
            blurProgram.dispose();
            blurProgram = null;
        }
        if(blurQuadBatch != null) {
            blurQuadBatch.dispose();
            blurQuadBatch = null;
        }
        initBlurSystem();
    }

    private MultiTargetFrameBuffer create(int bufferCount, MultiTargetFrameBuffer.Format format, float scale) {
        return MultiTargetFrameBuffer.create(format, bufferCount,
                (int) (width() * scale), (int)(height() * scale), true, false);
    }

    private MultiTargetFrameBuffer create(int bufferCount, Pixmap.Format format, float scale) {
        return MultiTargetFrameBuffer.create(format, bufferCount,
                (int)(width() * scale), (int)(height() * scale), true, false);
    }

    public float width() {
        return size.x;
    }
    public float height() {
        return size.y;
    }

    public int iWidth() {
        return (int) width();
    }
    public int iHeight() {
        return (int) height();
    }

    public int getDirLightCount() {
        return dirLightCount > 0 ? dirLightCount : 1;
    }

    public int getPointLightCount() {
        return pointLightCount > 0 ? pointLightCount : 1;
    }

    public int getSpotLightCount() {
        return spotLightCount > 0 ? spotLightCount : 1;
    }

    @Override
    public void dispose() {
        super.dispose();

        textureMRT.dispose();
        textureBatch.dispose();
        textureProgram.dispose();

        lightingMRT.dispose();
        lightingBatch.dispose();
        lightingProgram.dispose();

        disposeBlurSystem();

        quadProgram.dispose();

        Argent.removeOnResize(this::argentResize);
    }

    // Emissive stuff
    ShaderProgram blurProgram;
    SpriteBatch blurQuadBatch;
    MultiTargetFrameBuffer blurMRT;

    public void initBlurSystem() {
        blurMRT = create(1, Pixmap.Format.RGBA4444, GlobalSettings.ResolutionScale.blur());
        blurProgram = AppUtils.Shader.loadShader("pipeline/util/blur");
        blurQuadBatch = new SpriteBatch(2, blurProgram);
    }

    public void disposeBlurSystem() {
        blurMRT.dispose();
        blurProgram.dispose();
        blurQuadBatch.dispose();
    }

    public Texture generateBlurredEmissive() {
        boolean hor = false;
        int i;
        blurIteration();

        return blurMRT.getColorBufferTexture(0);
    }

    public void blurIteration() {
        blurProgram.begin();
        blurProgram.setUniformi("blurIteration", 100);
        Texture tex;
        tex = lightingMRT.getColorBufferTexture(ltg_EMISSIVE.id);
        tex.bind(1);
        blurProgram.setUniformi("image", 1);
        blurProgram.end();
        blurMRT.begin();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        blurQuadBatch.begin();
        blurQuadBatch.draw(tex, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        blurQuadBatch.end();
        if(Gdx.input.isKeyJustPressed(Input.Keys.F2))
            ScreenshotUtils.saveScreenshot(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), "Blur");
        blurMRT.end();
    }

    @Override
    public void onRefreshFBO(RefreshFBOEvent event) {
        refreshFBO();
    }

    public static class FBOAttachment {
        public int id;
        public String name;

        public FBOAttachment(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public void bind(ShaderProgram program) {
            bind(program, this.id);
        }
        public void bind(ShaderProgram program, int id) {
            program.setUniformi(this.name, id);
        }
    }

}
