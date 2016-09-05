package net.ncguy.argent.event.shader;

import net.ncguy.argent.assets.ArgShader;
import net.ncguy.argent.event.AbstractEvent;
import net.ncguy.argent.event.Subscribe;

/**
 * Created by Guy on 31/08/2016.
 */
public class ShaderSelectedEvent extends AbstractEvent {

    public ArgShader shader;

    public ShaderSelectedEvent() {}
    public ShaderSelectedEvent(ArgShader shader) { this.shader = shader; }

    public static interface ShaderSelectedListener {
        @Subscribe
        public void onShaderSeleced(ShaderSelectedEvent event);
    }

}
