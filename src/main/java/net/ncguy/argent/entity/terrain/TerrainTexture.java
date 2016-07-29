package net.ncguy.argent.entity.terrain;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Guy on 27/07/2016.
 */
public class TerrainTexture {

    private Map<SplatTexture.Channel, SplatTexture> textures;
    private SplatMap splatMap;
    private Terrain terrain;

    public TerrainTexture() {
        textures = new HashMap<>(5, 1);
    }

    public SplatTexture getTexture(SplatTexture.Channel channel) {
        return textures.get(channel);
    }

    public void removeTexture(SplatTexture.Channel channel) {
        textures.remove(channel);
        splatMap.clearChannel(channel);
        splatMap.updateTexture();
    }

    public void setSplatTexture(SplatTexture tex) {
        textures.put(tex.channel, tex);
    }

    public SplatTexture.Channel getNextFreeChannel() {
        // base
        SplatTexture st = textures.get(SplatTexture.Channel.BASE);
        if(st == null || st.texture.getId() == -1) return SplatTexture.Channel.BASE;
        // r
        st = textures.get(SplatTexture.Channel.R);
        if(st == null) return SplatTexture.Channel.R;
        // g
        st = textures.get(SplatTexture.Channel.G);
        if(st == null) return SplatTexture.Channel.G;
        // b
        st = textures.get(SplatTexture.Channel.B);
        if(st == null) return SplatTexture.Channel.B;
        // a
        st = textures.get(SplatTexture.Channel.A);
        if(st == null) return SplatTexture.Channel.A;

        return null;
    }

    public boolean hasTextureChannel(SplatTexture.Channel channel) {
        return textures.containsKey(channel);
    }

    public int countTextures() {
        return textures.size();
    }

    public SplatMap getSplatMap() {
        return splatMap;
    }

    public void setSplatMap(SplatMap splatMap) {
        this.splatMap = splatMap;
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
    }

}
