package net.ncguy.argent.editor.widgets.sidebar;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.layout.GridGroup;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import net.ncguy.argent.editor.EditorUI;
import net.ncguy.argent.render.argent.ArgentRenderer;
import net.ncguy.argent.utils.MultiTargetFrameBuffer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guy on 16/08/2016.
 */
public class DebugTab extends Tab {

    private Table rootContent;
    private Slider slider;
    private ScrollPane scroller;
    private GridGroup group;
    private EditorUI editorUI;
    List<Image> images;
    int imageCount = 0;
    String[] mrtNames;

    public DebugTab(EditorUI editorUI) {
        super(false, false);
        this.editorUI = editorUI;
        images = new ArrayList<>();
        rootContent = new Table(VisUI.getSkin()) {
            @Override
            public void act(float delta) {
                setDebug(true, true);
                super.act(delta);
                if(editorUI.getRenderer() instanceof ArgentRenderer) {
                    ArgentRenderer renderer = (ArgentRenderer) editorUI.getRenderer();
                    if(mrtNames == null) mrtNames = renderer.getMrtNames();
//                    MultiTargetFrameBuffer texMrt = renderer.getTextureMRT();
//                    MultiTargetFrameBuffer ltgMrt = renderer.getLightingMRT();
//                    MultiTargetFrameBuffer quadFbo = renderer.getQuadFBO();

                    MultiTargetFrameBuffer[] mrts = renderer.getMrts();

                    final int[] index = {0};
                    for (MultiTargetFrameBuffer mrt : mrts)
                        mrt.forEach(tex -> configure(tex, index[0]++));

                    if(images.size() > index[0]) {
                        for(int i = index[0]; i < images.size(); i++)
                            ((TextureRegionDrawable)images.remove(i).getDrawable()).getRegion().getTexture().dispose();
                    }
                }
                invalidateGrid();
            }
        };
        group = new GridGroup(){
            @Override
            public void act(float delta) {
                super.act(delta);
                group.setItemSize(slider.getVisualValue());
            }
        };
        group.setFillParent(true);
        scroller = new ScrollPane(group);

        slider = new Slider(8, 128, 8, false, VisUI.getSkin());
        slider.setAnimateDuration(.1f);

        rootContent.add(slider).expandX().fillX().row();
        rootContent.add(scroller).expand().fill().row();
    }

    private void configure(Texture tex, int index) {
//        System.out.println(index);
        Image img;
        if(images.size() <= index) {
            img = new Image();
            images.add(img);
            TextureRegion textureRegion = new TextureRegion(tex);
            textureRegion.setV(1);
            textureRegion.setV2(0);
            img.setDrawable(new TextureRegionDrawable(textureRegion));
        }else{
            img = images.get(index);
            Drawable drawable = img.getDrawable();
            if(drawable instanceof TextureRegionDrawable) {
                TextureRegion region = ((TextureRegionDrawable) drawable).getRegion();
//                region.getTexture().dispose();
                region.setTexture(tex);
            }
        }
    }

    private void invalidateGrid() {
        if(imageCount == images.size()) return;
        imageCount = images.size();
        group.clearChildren();
        final int[] index = {0};
        images.forEach(img -> {
            index[0] = MathUtils.clamp(index[0], 0, mrtNames.length-1);
            Table t = new Table(VisUI.getSkin());
            t.setBackground("menu-bg");
            String label = mrtNames[index[0]++];
            t.add(img).expand().fill().row();
            t.add(label).expandX().fillX().row();
            group.addActor(t);
        });
    }

    @Override
    public String getTabTitle() {
        return "Debug";
    }

    @Override
    public Table getContentTable() {
        return rootContent;
    }
}
