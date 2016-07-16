package net.ncguy.argent.particle;

import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.utils.Pool;

/**
 * Created by Guy on 16/07/2016.
 */
public class PFXPool extends Pool<ParticleEffect> {

    private ParticleEffect sourceEffect;

    public PFXPool(ParticleEffect sourceEffect) {
        this.sourceEffect = sourceEffect;
    }

    @Override
    public void free(ParticleEffect pfx) {
        pfx.reset();
        super.free(pfx);
    }

    @Override
    protected ParticleEffect newObject() {
        return sourceEffect.copy();
    }

}
