package net.ncguy.argent.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import net.ncguy.argent.data.Meta;
import net.ncguy.argent.data.config.ConfigControl;
import net.ncguy.argent.data.config.ConfigurableAttribute;
import net.ncguy.argent.data.config.IConfigurable;

import java.util.ArrayList;
import java.util.List;

import static com.badlogic.gdx.math.Matrix4.*;

/**
 * Created by Guy on 15/07/2016.
 */
public class TransformComponent implements Component, IConfigurable {

    public Matrix4 transform;
    public float roll, pitch, yaw;

    public TransformComponent() {
        this.transform = new Matrix4();
    }

    @Override
    public List<ConfigurableAttribute<?>> getConfigurableAttributes() {
        List<ConfigurableAttribute<?>> attrs = new ArrayList<>();
        attr(attrs, new Meta.Object("X", "Components|Transform|Translation"),  this::transX, this::transX, ConfigControl.NUMBERSELECTOR, Float::valueOf);
        attr(attrs, new Meta.Object("Y", "Components|Transform|Translation"),  this::transY, this::transY, ConfigControl.NUMBERSELECTOR, Float::valueOf);
        attr(attrs, new Meta.Object("Z", "Components|Transform|Translation"),  this::transZ, this::transZ, ConfigControl.NUMBERSELECTOR, Float::valueOf);

        attr(attrs, new Meta.Object("Roll",  "Components|Transform|Rotation"), this::roll,   this::roll,   ConfigControl.NUMBERSELECTOR, Float::valueOf);
        attr(attrs, new Meta.Object("Pitch", "Components|Transform|Rotation"), this::pitch,  this::pitch,  ConfigControl.NUMBERSELECTOR, Float::valueOf);
        attr(attrs, new Meta.Object("Yaw",   "Components|Transform|Rotation"), this::yaw,    this::yaw,    ConfigControl.NUMBERSELECTOR, Float::valueOf);

        attr(attrs, new Meta.Object("X", "Components|Transform|Scale"),        this::scaleX, this::scaleX, ConfigControl.NUMBERSELECTOR, Float::valueOf);
        attr(attrs, new Meta.Object("Y", "Components|Transform|Scale"),        this::scaleY, this::scaleY, ConfigControl.NUMBERSELECTOR, Float::valueOf);
        attr(attrs, new Meta.Object("Z", "Components|Transform|Scale"),        this::scaleZ, this::scaleZ, ConfigControl.NUMBERSELECTOR, Float::valueOf);
        return attrs;
    }

    public Vector3 trans() { return transform.getTranslation(new Vector3()); }

    public float transX() { return trans().x; }
    public float transY() { return trans().y; }
    public float transZ() { return trans().z; }

    public float roll()   { return roll; }
    public float pitch()  { return pitch; }
    public float yaw()    { return yaw; }

    public float scaleX() { return transform.getScaleX(); }
    public float scaleY() { return transform.getScaleY(); }
    public float scaleZ() { return transform.getScaleZ(); }

    public void transX(float val) { transform.getValues()[M03] = val; }
    public void transY(float val) { transform.getValues()[M13] = val; }
    public void transZ(float val) { transform.getValues()[M23] = val; }

    public void roll(float val)   { val %= 360; roll = val;  updateTransform(); }
    public void pitch(float val)  { val %= 360; pitch = val; updateTransform(); }
    public void yaw(float val)    { val %= 360; yaw = val;   updateTransform(); }

    public void scaleX(float val) { transform.getValues()[M00] = val; }
    public void scaleY(float val) { transform.getValues()[M11] = val; }
    public void scaleZ(float val) { transform.getValues()[M22] = val; }

    private void updateTransform() {
        Vector3 trans = trans();
        Vector3 scale = new Vector3();

        this.transform.getScale(scale);

        this.transform.setToScaling(1, 1, 1);
        this.transform.setToTranslation(0, 0, 0);

        this.transform.setFromEulerAngles(roll, pitch, yaw);
        this.transform.translate(trans);
        this.transform.scale(scale.x, scale.y, scale.z);
    }

}
