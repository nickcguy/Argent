package net.ncguy.argent.particle;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.graphics.g3d.particles.batches.PointSpriteParticleBatch;
import net.ncguy.argent.render.IRenderAddon;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guy on 16/07/2016.
 */
public class ParticleRenderer implements IRenderAddon {

    ParticleSystem system = new ParticleSystem();
    List<ParticleEffectWrapper> effects = new ArrayList<>();

    protected PointSpriteParticleBatch pointSpriteBatch = new PointSpriteParticleBatch();

    public void add(ParticleEffect effect, boolean autoStart) {
        add(wrap(effect), autoStart);
    }

    public void add(ParticleEffectWrapper wrapper, boolean autoStart) {
        wrapper.effect.init();
        if(autoStart) wrapper.effect.start();
        effects.add(wrapper);
        system.add(wrapper.effect);
    }

    public ParticleEffectWrapper wrap(ParticleEffect effect) {
        return new ParticleEffectWrapper(effect);
    }

    @Override
    public void init(Camera camera) {
        pointSpriteBatch.setCamera(camera);
        system.add(pointSpriteBatch);
    }

    @Override
    public void render(ModelBatch rootBatch, float delta) {
        system.begin();
        system.updateAndDraw();
        system.end();
        rootBatch.render(system);
    }

}
