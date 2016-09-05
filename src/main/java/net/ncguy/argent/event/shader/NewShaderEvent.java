package net.ncguy.argent.event.shader;

import net.ncguy.argent.assets.ArgShader;
import net.ncguy.argent.event.AbstractEvent;
import net.ncguy.argent.event.Subscribe;

/**
 * Created by Guy on 31/08/2016.
 */
public class NewShaderEvent extends AbstractEvent {

    public ArgShader shader;

    public NewShaderEvent() {}

    public NewShaderEvent(ArgShader shader) { this.shader = shader;}

    public static interface NewShaderListener {
        @Subscribe
        public void onNewShader(NewShaderEvent event);
    }

}
