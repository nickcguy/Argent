package net.ncguy.argent.editor.widgets;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.VisUI;
import net.ncguy.argent.utils.MultiTargetFrameBuffer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Guy on 16/08/2016.
 */
public class DebugPreview extends Table {

    private static DebugPreview instance;
    public static DebugPreview instance() {
        if (instance == null) {
            if(VisUI.isLoaded())
                instance = new DebugPreview();
        }
        return instance;
    }

    MultiTargetFrameBuffer[] mrts;
    Map<MultiTargetFrameBuffer, Image[]> images;

    public Map<MultiTargetFrameBuffer, Image[]> images() {
        if(images == null)
            images = new HashMap<>();
        return images;
    }

    public DebugPreview() {
        super(VisUI.getSkin());
    }

    @Override
    public void invalidate() {
        super.invalidate();
//        clearChildren();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        setDebug(true, true);
        images().forEach((mrt, imgs) -> {
            for (int i = 0; i < imgs.length; i++) {
                TextureRegion reg = new TextureRegion(mrt.getColorBufferTexture(i));
                reg.flip(false, true);
                imgs[i].setDrawable(new TextureRegionDrawable(reg));
            }
        });
    }

    public void build(MultiTargetFrameBuffer... mrts) {
        this.mrts = mrts;
        images().clear();
        clearChildren();
        for (MultiTargetFrameBuffer mrt : mrts) {
            Table table = new Table(VisUI.getSkin());
            Image[] img = new Image[mrt.bufferCount()];
            mrt.forEachIndexed((tex, i) -> {
                TextureRegion region = new TextureRegion(tex);
                region.flip(false, true);
                table.add(img[i] = new Image(region));
            });
            images().put(mrt, img);
            add(table).row();
        }
    }
}
