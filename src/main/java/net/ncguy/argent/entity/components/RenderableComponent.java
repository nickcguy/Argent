package net.ncguy.argent.entity.components;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import net.ncguy.argent.Argent;
import net.ncguy.argent.data.Meta;
import net.ncguy.argent.data.config.ConfigControl;
import net.ncguy.argent.data.config.ConfigurableAttribute;
import net.ncguy.argent.data.config.IConfigurable;
import net.ncguy.argent.ui.SearchableList;
import net.ncguy.argent.utils.TextureCache;

import java.util.List;

import static com.badlogic.gdx.math.Matrix4.*;
import static com.badlogic.gdx.math.Matrix4.M11;
import static com.badlogic.gdx.math.Matrix4.M22;

/**
 * Created by Guy on 15/07/2016.
 */
public class RenderableComponent extends ArgentComponent implements RenderableProvider, IConfigurable {

    protected ModelInstance instance;
    protected String modelRef;
    protected transient Matrix4 worldTransform;
    protected Matrix4 localTransform;

    protected float roll, pitch, yaw;

    public RenderableComponent() {
        this(new ModelInstance(new Model()));
    }

    public RenderableComponent(ModelInstance instance) {
        this.instance = instance;
        this.modelRef = "N/A";
        this.worldTransform = this.instance.transform;
        this.localTransform = new Matrix4();
    }

    public RenderableComponent(String modelRef) {
        this.instance = new ModelInstance(new Model());
        this.modelRef = modelRef;
        this.worldTransform = this.instance.transform;
        this.localTransform = new Matrix4();
        setModel(modelRef);
    }

    public void setModel(String ref) {
        Model model = Argent.content.get(ref, Model.class);
        if(model == null) {
            this.modelRef = "N/A";
            return;
        }
        setModel(model);
        this.modelRef = ref;
    }

    public void setModel(Model model) {
        Material[] mtls = this.instance.materials.toArray(Material.class);

        this.instance.model.nodes.clear();
        this.instance.model.nodes.addAll(model.nodes);

        this.instance.nodes.clear();
        this.instance.nodes.addAll(model.nodes);

        setMaterials(mtls);
    }

    public void setMaterial(final int index, final Material mtl) {
        final int[] i = {0};
        this.instance.nodes.forEach(n -> n.parts.forEach(p -> {
            if(i[0] == index) p.material = mtl;
            i[0]++;
        }));
    }

    public void setMaterials(Material... mtls) {
        int index = 0;
        for (Material mtl : mtls)
            setMaterial(index++, mtl);
    }

    public ModelInstance instance() { return instance; }

    @Override
    public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
        this.worldTransform.set(parent().transform().cpy());

        Vector3 trn     = this.localTransform.getTranslation(new Vector3());
        Quaternion rot  = this.localTransform.getRotation(new Quaternion());
        Vector3 scl     = this.localTransform.getScale(new Vector3());

        roll = rot.getRoll();
        pitch = rot.getPitch();
        yaw = rot.getYaw();

        this.worldTransform.translate(trn).scale(scl.x, scl.y, scl.z);
//        this.worldTransform.mul(this.localTransform);

        instance().getRenderables(renderables, pool);
    }

    protected void addRenderableTransformAttributes(String path, List<ConfigurableAttribute<?>> attrs) {
        attr(attrs, new Meta.Object("X", "Components|"+path+"|Transform|Translation"),  this::transX, this::transX, ConfigControl.NUMBERSELECTOR, Float::valueOf);
        attr(attrs, new Meta.Object("Y", "Components|"+path+"|Transform|Translation"),  this::transY, this::transY, ConfigControl.NUMBERSELECTOR, Float::valueOf);
        attr(attrs, new Meta.Object("Z", "Components|"+path+"|Transform|Translation"),  this::transZ, this::transZ, ConfigControl.NUMBERSELECTOR, Float::valueOf);

//        attr(attrs, new Meta.Object("Roll",  "Components|"+path+"|Transform|Rotation"), this::roll,   this::roll,   ConfigControl.NUMBERSELECTOR, this::rotationTunnel);
//        attr(attrs, new Meta.Object("Pitch", "Components|"+path+"|Transform|Rotation"), this::pitch,  this::pitch,  ConfigControl.NUMBERSELECTOR, this::rotationTunnel);
//        attr(attrs, new Meta.Object("Yaw",   "Components|"+path+"|Transform|Rotation"), this::yaw,    this::yaw,    ConfigControl.NUMBERSELECTOR, this::rotationTunnel);

        attr(attrs, new Meta.Object("X", "Components|"+path+"|Transform|Scale"),        this::scaleX, this::scaleX, ConfigControl.NUMBERSELECTOR, Float::valueOf);
        attr(attrs, new Meta.Object("Y", "Components|"+path+"|Transform|Scale"),        this::scaleY, this::scaleY, ConfigControl.NUMBERSELECTOR, Float::valueOf);
        attr(attrs, new Meta.Object("Z", "Components|"+path+"|Transform|Scale"),        this::scaleZ, this::scaleZ, ConfigControl.NUMBERSELECTOR, Float::valueOf);
    }

    @Override
    public void getConfigurableAttributes(List<ConfigurableAttribute<?>> attrs) {
        ConfigurableAttribute<String> modelRefAttr = attr(attrs, new Meta.Object("Model Ref", "Components|Renderable"), this::modelRef, this::modelRef, ConfigControl.SELECTIONLIST);
        String[] refs = Argent.content.getAllRefs(Model.class);
        SearchableList.Item<String>[] items = new SearchableList.Item[refs.length];
        int index = 0;
        for (String ref : refs) {
            items[index++] = new SearchableList.Item<>(new TextureRegionDrawable(new TextureRegion(TextureCache.pixel())), ref, ref, ref);
        }
        modelRefAttr.addParam("items", SearchableList.Item[].class, items);

        addRenderableTransformAttributes("Renderable", attrs);

    }

    public String modelRef() { return this.modelRef; }
    public void modelRef(String modelRef) {
        setModel(modelRef);
    }


    protected float positiveTunnel(String s) {
        return Math.abs(Float.parseFloat(s));
    }


    public float rotationTunnel(String s) {
        float r = Float.valueOf(s);
        r += 360;
        r %= 360;
        return r;
    }

    public Vector3 trans() { return localTransform.getTranslation(new Vector3()); }

    public float transX() { return trans().x; }
    public float transY() { return trans().y; }
    public float transZ() { return trans().z; }

    public float roll()   { return roll; }
    public float pitch()  { return pitch; }
    public float yaw()    { return yaw; }

    public float scaleX() { return localTransform.getScaleX(); }
    public float scaleY() { return localTransform.getScaleY(); }
    public float scaleZ() { return localTransform.getScaleZ(); }

    public void transX(float val) { localTransform.getValues()[M03] = val; }
    public void transY(float val) { localTransform.getValues()[M13] = val; }
    public void transZ(float val) { localTransform.getValues()[M23] = val; }

    public void roll(float val)   { val %= 360; roll = val;  updateTransform(); }
    public void pitch(float val)  { val %= 360; pitch = val; updateTransform(); }
    public void yaw(float val)    { val %= 360; yaw = val;   updateTransform(); }

    public void scaleX(float val) { localTransform.getValues()[M00] = val; }
    public void scaleY(float val) { localTransform.getValues()[M11] = val; }
    public void scaleZ(float val) { localTransform.getValues()[M22] = val; }

    private void updateTransform() {
        Vector3 trans = trans();
        Vector3 scale = new Vector3();

        this.localTransform.getScale(scale);

        this.localTransform.setToScaling(1, 1, 1);
        this.localTransform.setToTranslation(0, 0, 0);

        this.localTransform.setFromEulerAngles(roll, pitch, yaw);
        this.localTransform.translate(trans);
        this.localTransform.scale(scale.x, scale.y, scale.z);
    }

}
