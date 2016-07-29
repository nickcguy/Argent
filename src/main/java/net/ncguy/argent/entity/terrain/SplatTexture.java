package net.ncguy.argent.entity.terrain;

import com.badlogic.gdx.graphics.Texture;
import net.ncguy.argent.assets.ArgTexture;
import net.ncguy.argent.assets.ArgTextureProvider;

/**
 * Created by Guy on 27/07/2016.
 */
public class SplatTexture implements ArgTextureProvider {

    public enum Channel {
        BASE, R, G, B, A
    }

    public Channel channel;
    public ArgTexture texture;

    public SplatTexture(Channel channel) {
        this.channel = channel;
    }

    public SplatTexture(Channel channel, ArgTexture texture) {
        this.channel = channel;
        this.texture = texture;
    }

    @Override
    public Texture getTexture() {
        if(texture != null)
            return texture.getTexture();
        return null;
    }

}
