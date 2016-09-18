package net.ncguy.argent.entity.components.light;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import net.ncguy.argent.GlobalSettings;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.entity.components.ArgentComponent;
import net.ncguy.argent.render.argent.DepthMapRenderer;
import net.ncguy.argent.utils.AppUtils;

import static net.ncguy.argent.GlobalSettings.VarKeys.bool_LIGHTDEBUG;

/**
 * Created by Guy on 29/07/2016.
 */
public abstract class LightComponent implements ArgentComponent {

    WorldEntity entity;
    Vector3 worldPosition;
    Vector3 localPosition;

    Color ambient, diffuse, specular;
    float intensity;
    float clipRange;
    private boolean dirtyShadows;

    protected Model debugModel;
    protected ModelInstance debugInstance;
    protected ModelInstance debugInstance() {
        if (debugInstance == null) {
            debugModel = new ModelBuilder().createBox(.1f, .1f, .1f, new Material(ColorAttribute.createDiffuse(AppUtils.Graphics.randomColour())), VertexAttributes.Usage.Position);
            debugInstance = new ModelInstance(debugModel);
        }
        return debugInstance;
    }

    protected void invalidateDebug() {
        if(debugModel != null) {
            debugModel.dispose();
            debugModel = null;
        }
        debugInstance = null;
    }

    public LightComponent(WorldEntity entity) {
        this.entity = entity;
        this.localPosition = new Vector3(0, 0, 0);
        this.entity.getPosition(worldPosition = new Vector3());
        this.ambient = new Color();
        this.diffuse = new Color();
        this.specular = new Color();
        this.intensity = 1.0f;
        this.clipRange = 10.0f;
    }

    public boolean isDirtyShadows() { return !dirtyShadows; }
    public void setDirtyShadows(boolean dirtyShadows) { this.dirtyShadows = dirtyShadows; }

    @Override
    public WorldEntity getWorldEntity() {
        return this.entity;
    }

    @Override public void update(float delta) {
    }

    @Override
    public Type getType() {
        return Type.LIGHT;
    }

    @Override
    public void setType(Type type) {
        // NOOP
    }

    @Override
    public void remove() {
        this.entity.remove(this);
    }

    public Vector3 getLocalPosition() {
        return localPosition;
    }

    public Vector3 getWorldPosition() {
        return entity.getPosition(worldPosition).cpy().add(localPosition);
    }

    public Color getDiffuse() {
        return diffuse;
    }

    public Color getAmbient() { return ambient; }

    public void setAmbient(Color ambient) { this.ambient = ambient; }

    public Color getSpecular() { return specular; }

    public void setSpecular(Color specular) { this.specular = specular; }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    @Override
    public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
        if(GlobalSettings.hasBoolVar(bool_LIGHTDEBUG)) {
            debugInstance().transform.setToTranslation(getWorldPosition());
            debugInstance().getRenderables(renderables, pool);
        }
    }

    @Override
    public void dispose() {
        if(debugInstance != null) {
            debugInstance = null;
        }
        if(debugModel != null) {
            debugModel.dispose();
            debugModel = null;
        }
    }

    public boolean usePosition() {
        return true;
    }

    public void bindShadow(DepthMapRenderer renderer) {
        selectCamera(renderer).position.set(getWorldPosition());
        selectCamera(renderer).far = clipRange;
        selectCamera(renderer).update(true);
    }

    public Camera selectCamera(DepthMapRenderer renderer) {
        return renderer.camera();
    }

    public GLFrameBuffer selectFBO(DepthMapRenderer renderer) {
        return renderer.depthFBO();
    }

    public void bind(ShaderProgram program, String prefix) {
        if(usePosition()) bindVector3(program, prefix+".Position", getWorldPosition());
        bindColour(program, prefix+".Ambient", getAmbient());
        bindColour(program, prefix+".Diffuse", getDiffuse());
        bindColour(program, prefix+".Specular", getSpecular());
        bindFloat(program, prefix+".Intensity", getIntensity());
    }

    public void bindFloat(ShaderProgram program, String key, float val) {
        program.setUniformf(key, val);
    }

    public void bindVector3(ShaderProgram program, String key, Vector3 vec) {
        float[] val = new float[] { vec.x, vec.y, vec.z };
        program.setUniform3fv(key, val, 0, val.length);
    }
    public void bindColour(ShaderProgram program, String key, Color col) {
        float[] val = new float[] { col.r, col.g, col.b };
        program.setUniform3fv(key, val, 0, val.length);
    }
}
