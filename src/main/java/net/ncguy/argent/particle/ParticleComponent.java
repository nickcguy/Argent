package net.ncguy.argent.particle;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import net.ncguy.argent.data.config.ConfigurableAttribute;
import net.ncguy.argent.data.config.IConfigurable;

import java.util.List;

/**
 * Created by Guy on 16/07/2016.
 */
public class ParticleComponent implements Component, IConfigurable {

    ParticleSystem system;

    public ParticleComponent() {
        this.system = new ParticleSystem();
    }

    public ParticleComponent(ParticleSystem system) { this.system = system;}

    @Override
    public void getConfigurableAttributes(List<ConfigurableAttribute<?>> attrs) {
    }

}