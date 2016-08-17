package net.ncguy.argent.editor.widgets.sidebar;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
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

    public DebugTab(EditorUI editorUI) {
        super(false, false);
        this.editorUI = editorUI;
        images = new ArrayList<>();
        rootContent = new Table(VisUI.getSkin()) {
            @Override
            public void act(float delta) {
                super.act(delta);
                if(editorUI.getRenderer() instanceof ArgentRenderer) {
                    MultiTargetFrameBuffer texMrt = ((ArgentRenderer) editorUI.getRenderer()).getTextureMRT();
                    MultiTargetFrameBuffer ltgMrt = ((ArgentRenderer) editorUI.getRenderer()).getLightingMRT();
                    final int[] index = {0};
                    texMrt.forEach(tex -> configure(tex, index[0]++));
                    ltgMrt.forEach(tex -> configure(tex, index[0]++));
                    if(images.size() > index[0]) {
                        for(int i = index[0]; i < images.size(); i++)
                            images.remove(i);
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
        Image img;
        if(images.size() <= index) {
            img = new Image();
            images.add(img);
        }else{
            img = images.get(index);
        }
        TextureRegion textureRegion = new TextureRegion(tex);
        textureRegion.setV(1);
        textureRegion.setV2(0);
        img.setDrawable(new TextureRegionDrawable(textureRegion));

    }

    private void invalidateGrid() {
        if(imageCount == images.size()) return;
        imageCount = images.size();
        group.clearChildren();
        images.forEach(group::addActor);
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
