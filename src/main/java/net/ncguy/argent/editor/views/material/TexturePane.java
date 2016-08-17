package net.ncguy.argent.editor.views.material;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.layout.GridGroup;
import net.ncguy.argent.assets.ArgTexture;
import net.ncguy.argent.editor.EditorUI;
import net.ncguy.argent.ui.AssetItem;
import net.ncguy.argent.ui.Icons;

/**
 * Created by Guy on 02/08/2016.
 */
public class TexturePane extends Table {

    private GridGroup gridGroup;
    private ScrollPane scroller;
    private Slider zoomSlider;
    private Button syncBtn;

    private EditorUI editorUI;

    public TexturePane(EditorUI editorUI) {
        super(VisUI.getSkin());
        this.editorUI = editorUI;
        setBackground("default-pane");
        gridGroup = new GridGroup(64, 4);
        scroller = new ScrollPane(gridGroup, VisUI.getSkin());
        scroller.setFlickScroll(false);
        scroller.setScrollingDisabled(true, false);
        scroller.setFadeScrollBars(false);

        zoomSlider = new Slider(16, 256, 16, false, VisUI.getSkin()) {
            @Override
            public void act(float delta) {
                super.act(delta);
                float v = zoomSlider.getVisualValue();
                if(gridGroup.getItemHeight() != v)
                    gridGroup.setItemSize(v);
            }
        };
        zoomSlider.setValue(64);
        zoomSlider.setAnimateDuration(.1f);

        syncBtn = new ImageButton(VisUI.getSkin());
        Image i = new Image(Icons.Icon.REFRESH.drawable());
        i.setSize(32, 32);
        syncBtn.addActor(i);
        syncBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                loadTextures();
            }
        });

        add(zoomSlider).left().padRight(5).expandX().fillX();
        add(syncBtn).right().size(32).row();
        add(scroller).colspan(2).expand().fill().row();

        loadTextures();
    }

    public void loadTextures() {
        gridGroup.clearChildren();
        System.out.println("Loading textures");
        editorUI.projectManager.global().textures().forEach(tex -> {
            AssetItem<ArgTexture> item = new AssetItem<>(tex);
            gridGroup.addActor(item);
        });
    }

}
