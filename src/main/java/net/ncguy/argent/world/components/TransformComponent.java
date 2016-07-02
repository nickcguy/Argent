package net.ncguy.argent.world.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;
import net.ncguy.argent.editor.ConfigurableAttribute;
import net.ncguy.argent.editor.IConfigurable;
import net.ncguy.argent.editor.swing.config.ConfigControl;
import net.ncguy.argent.utils.Reference;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guy on 21/06/2016.
 */
public class TransformComponent implements Component, Pool.Poolable, IConfigurable {

    public Matrix4 transform;

    public float rotRoll = 0;
    public float rotPitch = 0;
    public float rotYaw = 0;

    public TransformComponent() {
        this(new Matrix4());
    }

    public TransformComponent(Matrix4 transform) {
        this.transform = transform;
    }

    @Override
    public void reset() {
        this.transform.setToScaling(1, 1, 1);
        this.transform.setToTranslation(0, 0, 0);
        this.transform.setFromEulerAngles(0, 0, 0);

        rotRoll = 0;
        rotPitch = 0;
        rotYaw = 0;
    }

    public void updateTransform() {
        Vector3 trans = new Vector3();
        Vector3 scale = new Vector3();
        this.transform.getTranslation(trans);
        this.transform.getScale(scale);

        this.transform.setToScaling(1, 1, 1);
        this.transform.setToTranslation(0, 0, 0);

        this.transform.setFromEulerAngles(rotRoll, rotPitch, rotYaw);
        this.transform.translate(trans);
        this.transform.scale(scale.x, scale.y, scale.z);

    }

    @Override
    public List<ConfigurableAttribute<?>> getConfigurableAttributes() {
        List<ConfigurableAttribute<?>> attrs = new ArrayList<>();

        attrs.add(attr("Translation X",   () -> transform.getValues()[Reference.Matrix4Alias.TranslationX],   (var) -> transform.getValues()[Reference.Matrix4Alias.TranslationX] = var, ConfigControl.NUMBERSELECTOR));
        attrs.add(attr("Translation Y",   () -> transform.getValues()[Reference.Matrix4Alias.TranslationY],   (var) -> transform.getValues()[Reference.Matrix4Alias.TranslationY] = var, ConfigControl.NUMBERSELECTOR));
        attrs.add(attr("Translation Z",   () -> transform.getValues()[Reference.Matrix4Alias.TranslationZ],   (var) -> transform.getValues()[Reference.Matrix4Alias.TranslationZ] = var, ConfigControl.NUMBERSELECTOR));

        attrs.add(attr("Rotation Roll",   () -> rotRoll,                                                      (val) -> { rotRoll =val; updateTransform(); }, ConfigControl.NUMBERSELECTOR));
        attrs.add(attr("Rotation Pitch",  () -> rotPitch,                                                     (val) -> { rotPitch=val; updateTransform(); }, ConfigControl.NUMBERSELECTOR));
        attrs.add(attr("Rotation Yaw",    () -> rotYaw,                                                       (val) -> { rotYaw  =val; updateTransform(); }, ConfigControl.NUMBERSELECTOR));

        attrs.add(attr("Scale X",         () -> transform.getValues()[Reference.Matrix4Alias.ScaleX],         (var) -> transform.getValues()[Reference.Matrix4Alias.ScaleX] = var, ConfigControl.NUMBERSELECTOR));
        attrs.add(attr("Scale Y",         () -> transform.getValues()[Reference.Matrix4Alias.ScaleY],         (var) -> transform.getValues()[Reference.Matrix4Alias.ScaleY] = var, ConfigControl.NUMBERSELECTOR));
        attrs.add(attr("Scale Z",         () -> transform.getValues()[Reference.Matrix4Alias.ScaleZ],         (var) -> transform.getValues()[Reference.Matrix4Alias.ScaleZ] = var, ConfigControl.NUMBERSELECTOR));

        return attrs;
    }
}
