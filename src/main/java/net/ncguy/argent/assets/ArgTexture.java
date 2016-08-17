package net.ncguy.argent.assets;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * Created by Guy on 27/07/2016.
 */
public class ArgTexture extends ArgAsset<Texture> implements ArgTextureProvider {

    private long id;
    private String path;

    public Texture texture;

    private String name = "";

    public ArgTexture(Texture texture) {
        this.texture = texture;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    @Override
    public Texture getTexture() {
        return texture;
    }

    @Override
    public String tag() {
        return "tex";
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public void name(String name) {
        this.name = name;
    }

    @Override
    public Drawable icon() {
        return new TextureRegionDrawable(new TextureRegion(texture));
    }
}
