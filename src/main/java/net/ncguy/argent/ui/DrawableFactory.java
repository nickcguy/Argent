package net.ncguy.argent.ui;

        import com.badlogic.gdx.graphics.Texture;
        import com.badlogic.gdx.graphics.g2d.TextureRegion;
        import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
        import net.ncguy.argent.Argent;

/**
 * Created by Guy on 18/08/2016.
 */
public class DrawableFactory {

    public static TiledDrawable grid() {
        return new TiledDrawable(new TextureRegion(Argent.content.get("Texture_grid", Texture.class)));
    }

}