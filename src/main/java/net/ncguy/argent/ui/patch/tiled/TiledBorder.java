package net.ncguy.argent.ui.patch.tiled;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import net.ncguy.argent.Argent;

/**
 * Created by Guy on 01/08/2016.
 */
public class TiledBorder extends BaseDrawable {

    protected TiledDrawable[] tiles = new TiledDrawable[9];

    public TiledBorder(String ref) {
        for (int i = 0; i < tiles.length; i++) {
            tiles[i] = new TiledDrawable(new TextureRegion(Argent.content.get(ref+"_"+i, Texture.class)));
        }
    }

    @Override
    public void draw(Batch batch, float x, float y, float width, float height) {
        super.draw(batch, x, y, width, height);

        float w = width-16;
        float h = height-16;

        tiles[0].draw(batch, x, y, 16, 16);
        tiles[1].draw(batch, x+16, y, w, 16);
        tiles[2].draw(batch, x+w, y, 16, 16);
        tiles[3].draw(batch, x+w, y+16, 16, h);
        tiles[4].draw(batch, x+w, y+h, 16, 16);
        tiles[5].draw(batch, x+16, y+h, w, 16);
        tiles[6].draw(batch, x, y+h, 16, 16);
        tiles[7].draw(batch, x, y+16, 16, h);
        tiles[8].draw(batch, x+16, y+16, w, h);

    }
}
