package net.ncguy.argent.assets;

import com.badlogic.gdx.graphics.Texture;

/**
 * Created by Guy on 27/07/2016.
 */
public class ArgTexture implements ArgTextureProvider {

    private long id;
    private String path;

    public Texture texture;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    @Override
    public Texture getTexture() {
        return texture;
    }
}
