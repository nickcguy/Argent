package net.ncguy.argent.particle;

import com.badlogic.gdx.graphics.g3d.particles.ParticleController;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import net.ncguy.argent.data.Meta;
import net.ncguy.argent.data.config.ConfigControl;
import net.ncguy.argent.data.config.ConfigurableAttribute;
import net.ncguy.argent.data.config.IConfigurable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guy on 16/07/2016.
 */
public class ParticleEffectWrapper implements IConfigurable {

    public ParticleEffect effect;

    public ParticleEffectWrapper(ParticleEffect effect) {
        this.effect = effect;
    }

    @Override
    public List<ConfigurableAttribute<?>> getConfigurableAttributes() {
        List<ConfigurableAttribute<?>> attrs = new ArrayList<>();
        attr(attrs, new Meta.Object("X", "Bounds|Min"), this::minX, this::minX, ConfigControl.NUMBERSELECTOR, Float::valueOf);
        attr(attrs, new Meta.Object("Y", "Bounds|Min"), this::minY, this::minY, ConfigControl.NUMBERSELECTOR, Float::valueOf);
        attr(attrs, new Meta.Object("Z", "Bounds|Min"), this::minZ, this::minZ, ConfigControl.NUMBERSELECTOR, Float::valueOf);

        attr(attrs, new Meta.Object("X", "Bounds|Max"), this::maxX, this::maxX, ConfigControl.NUMBERSELECTOR, Float::valueOf);
        attr(attrs, new Meta.Object("Y", "Bounds|Max"), this::maxY, this::maxY, ConfigControl.NUMBERSELECTOR, Float::valueOf);
        attr(attrs, new Meta.Object("Z", "Bounds|Max"), this::maxZ, this::maxZ, ConfigControl.NUMBERSELECTOR, Float::valueOf);
        return attrs;
    }

    public float minX() { return effect.getBoundingBox().min.x; }
    public float minY() { return effect.getBoundingBox().min.y; }
    public float minZ() { return effect.getBoundingBox().min.z; }

    public float maxX() { return effect.getBoundingBox().max.x; }
    public float maxY() { return effect.getBoundingBox().max.y; }
    public float maxZ() { return effect.getBoundingBox().max.z; }


    public void minX(float val) { effect.getBoundingBox().min.x = val; }
    public void minY(float val) { effect.getBoundingBox().min.y = val; }
    public void minZ(float val) { effect.getBoundingBox().min.z = val; }

    public void maxX(float val) { effect.getBoundingBox().max.x = val; }
    public void maxY(float val) { effect.getBoundingBox().max.y = val; }
    public void maxZ(float val) { effect.getBoundingBox().max.z = val; }

    public static class ControllerWrapper implements IConfigurable {

        ParticleController controller;

        public ControllerWrapper(ParticleController controller) {
            this.controller = controller;
        }

        @Override
        public List<ConfigurableAttribute<?>> getConfigurableAttributes() {
            List<ConfigurableAttribute<?>> attrs = new ArrayList<>();



            return attrs;
        }

        public String name() { return controller.name; }


        public void name(String val) { controller.name = val; }

    }

}
