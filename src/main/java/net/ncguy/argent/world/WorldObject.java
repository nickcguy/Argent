package net.ncguy.argent.world;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import net.ncguy.argent.Argent;
import net.ncguy.argent.core.Meta;
import net.ncguy.argent.editor.ConfigAttr;
import net.ncguy.argent.editor.ConfigurableAttribute;
import net.ncguy.argent.editor.IConfigurable;
import net.ncguy.argent.editor.shared.config.ConfigControl;
import net.ncguy.argent.editor.swing.VisualEditorRoot;
import net.ncguy.argent.io.IWritable;
import net.ncguy.argent.ui.SearchableList;
import net.ncguy.argent.utils.Reference;

import java.util.ArrayList;
import java.util.List;

import static net.ncguy.argent.editor.shared.config.ConfigControl.TEXTFIELD;

/**
 * Created by Guy on 20/06/2016.
 */
public class WorldObject implements IConfigurable, IWritable {

    public transient GameWorld.Generic<WorldObject> gameWorld;
    public transient ModelInstance instance;

    @ConfigAttr(control = TEXTFIELD)
    @Meta(displayName = "Display Name", category = "Data")
    public String displayName = "Test";
    public Matrix4 transform;
    public float rotRoll = 0;
    public float rotPitch = 0;
    public float rotYaw = 0;
    public List<Material> materials;
    public String modelRef;

    public void setModel(String ref) {
        Model model = Argent.content.get(ref, Model.class);
        if(model == null) return;
        setModel(model);
        modelRef = ref;
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

    public void setModelInstance(Model model) {
        ModelInstance newInst = new ModelInstance(model);
        newInst.transform = transform;
        newInst.materials.clear();
        newInst.materials.addAll(this.instance.materials);
        this.instance.nodes.clear();
        this.instance.nodes.addAll(newInst.nodes);
        if(VisualEditorRoot.recentEditor != null) {
            VisualEditorRoot.recentEditor.shaderEditor.compile();
        }
    }

    public String getModelRef() {
        return this.modelRef;
    }

    @Override
    public String toString() {
        return displayName.length() == 0 ? getClass().getSimpleName() : displayName;
    }

    public void updateTransform() {
        Vector3 trans = new Vector3();
        Vector3 scale = new Vector3();
        this.transform().getTranslation(trans);
        this.transform().getScale(scale);

        this.transform().setToScaling(1, 1, 1);
        this.transform().setToTranslation(0, 0, 0);

        this.transform().setFromEulerAngles(rotRoll, rotPitch, rotYaw);
        this.transform().translate(trans);
        this.transform().scale(scale.x, scale.y, scale.z);
    }

    public WorldObject(GameWorld.Generic<WorldObject> gameWorld, ModelInstance inst, String modelRef) {
        this(gameWorld, inst);
        setModel(modelRef);
    }

    public WorldObject(GameWorld.Generic<WorldObject> gameWorld, ModelInstance inst) {
        this.gameWorld = gameWorld;

        this.instance = inst;
        this.transform = inst.transform;
    }



    public void translate(Vector3 offset) {
        transform.translate(offset);
        instance.transform = this.transform;
    }

    public Matrix4 transform() {
        if(instance != null) {
            instance.transform = transform;
        }
        return this.transform;
    }

    @Override
    public List<ConfigurableAttribute<?>> getConfigurableAttributes() {
        List<ConfigurableAttribute<?>> attrs = new ArrayList<>();

        attr(attrs, new Meta.Object("Translation X", "Transform"),   () -> ((Float)transform().getValues()[Reference.Matrix4Alias.TranslationX]).intValue(),   (val) -> { transform().getValues()[Reference.Matrix4Alias.TranslationX] = Float.parseFloat(val.toString()); }, ConfigControl.NUMBERSELECTOR, Float::parseFloat);
        attr(attrs, new Meta.Object("Translation Y", "Transform"),   () -> ((Float)transform().getValues()[Reference.Matrix4Alias.TranslationY]).intValue(),   (val) -> { transform().getValues()[Reference.Matrix4Alias.TranslationY] = Float.parseFloat(val.toString()); }, ConfigControl.NUMBERSELECTOR, Float::parseFloat);
        attr(attrs, new Meta.Object("Translation Z", "Transform"),   () -> ((Float)transform().getValues()[Reference.Matrix4Alias.TranslationZ]).intValue(),   (val) -> { transform().getValues()[Reference.Matrix4Alias.TranslationZ] = Float.parseFloat(val.toString()); }, ConfigControl.NUMBERSELECTOR, Float::parseFloat);

        attr(attrs, new Meta.Object("Rotation Roll", "Transform"),   () -> ((Float)rotRoll).intValue(),                                                      (val) -> { rotRoll =Float.parseFloat(val.toString()); updateTransform(); }, ConfigControl.NUMBERSELECTOR, Float::parseFloat);
        attr(attrs, new Meta.Object("Rotation Pitch", "Transform"),  () -> ((Float)rotPitch).intValue(),                                                     (val) -> { rotPitch=Float.parseFloat(val.toString()); updateTransform(); }, ConfigControl.NUMBERSELECTOR, Float::parseFloat);
        attr(attrs, new Meta.Object("Rotation Yaw", "Transform"),    () -> ((Float)rotYaw).intValue(),                                                       (val) -> { rotYaw  =Float.parseFloat(val.toString()); updateTransform(); }, ConfigControl.NUMBERSELECTOR, Float::parseFloat);

        attr(attrs, new Meta.Object("Scale X", "Transform"),         () -> ((Float)transform().getValues()[Reference.Matrix4Alias.ScaleX]).intValue(),         (val) -> { transform().getValues()[Reference.Matrix4Alias.ScaleX] = Float.parseFloat(val.toString()); }, ConfigControl.NUMBERSELECTOR, Float::parseFloat);
        attr(attrs, new Meta.Object("Scale Y", "Transform"),         () -> ((Float)transform().getValues()[Reference.Matrix4Alias.ScaleY]).intValue(),         (val) -> { transform().getValues()[Reference.Matrix4Alias.ScaleY] = Float.parseFloat(val.toString()); }, ConfigControl.NUMBERSELECTOR, Float::parseFloat);
        attr(attrs, new Meta.Object("Scale Z", "Transform"),         () -> ((Float)transform().getValues()[Reference.Matrix4Alias.ScaleZ]).intValue(),         (val) -> { transform().getValues()[Reference.Matrix4Alias.ScaleZ] = Float.parseFloat(val.toString()); }, ConfigControl.NUMBERSELECTOR, Float::parseFloat);

        ConfigurableAttribute modelAttr = attr(attrs, new Meta.Object("Model", "Rendering"), this::getModelRef, this::setModel, ConfigControl.SELECTIONLIST);
        String[] modelRefs = Argent.content.getAllRefs(Model.class);
        SearchableList.Item.Data[] modelItems = new SearchableList.Item.Data[modelRefs.length];
        int index = 0;
        for (String ref : modelRefs)
            modelItems[index++] = new SearchableList.Item.Data<>(new TextureRegionDrawable(new TextureRegion(net.ncguy.argent.utils.SpriteCache.pixel())), ref, ref);
        modelAttr.addParam("items", SearchableList.Item.Data[].class, modelItems);

        return attrs;
    }


    @Override
    public void packData() {
        this.materials = new ArrayList<>();
        this.instance.nodes.forEach(n -> n.parts.forEach(p -> {
            if(p.material != null) this.materials.add(p.material);
        }));
    }

    @Override
    public void unpackData() {
        final int[] index = {0};

        if(this.instance == null) this.instance = new ModelInstance(new Model());

        setModel(modelRef);

        this.instance.nodes.forEach(n -> n.parts.forEach(p -> {
            if(this.materials.size() < index[0]) p.material = this.materials.get(index[0]);
            index[0]++;
        }));
        this.materials.clear();
        this.materials = null;
    }

    public Vector3 getTranslation() {
        Vector3 trans = new Vector3();
        transform.getTranslation(trans);
        return trans;
    }

    public void rotateTo(Vector3 flatForwardVector) {

    }



}
