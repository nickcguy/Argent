package net.ncguy.argent.editor.panels;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import net.ncguy.argent.editor.EditorRoot;
import net.ncguy.argent.utils.TextureCache;

/**
 * Created by Guy on 18/07/2016.
 */
public class ObjectDataPanel<T> extends AbstractEditorPanel<T> {

    private Image bgImage;
    private Image previewImage;

    public ObjectDataPanel(EditorRoot<T> editorRoot) {
        super(editorRoot, "");
    }

    public ObjectDataPanel(EditorRoot<T> editorRoot, String name) {
        super(editorRoot, name);
    }

    public ObjectDataPanel(EditorRoot<T> editorRoot, String name, boolean savable) {
        super(editorRoot, name, savable);
    }

    public ObjectDataPanel(EditorRoot<T> editorRoot, String name, boolean savable, boolean closeableByUser) {
        super(editorRoot, name, savable, closeableByUser);
    }

    @Override
    protected void init() {
        this.name = "Object Data";
        this.group = new Group();

        this.bgImage = new Image(new TextureRegionDrawable(new TextureRegion(TextureCache.pixel())));
        this.bgImage.setColor(.32f, .32f, .32f, 1);
        this.previewImage = new Image(TextureCache.pixel());

        this.group.addActor(this.bgImage);
        this.group.addActor(this.previewImage);
    }

    @Override
    public void onSwitchTo() {
        editorRoot.cacheCamera();
//        editorRoot.resizeCamera(100, 100);
    }

    @Override
    public void onSwitchFrom() {
        editorRoot.revertCamera();
    }

    @Override
    public void resize(int w, int h) {
        this.bgImage.setBounds(0, 0, w, h);
        this.previewImage.setBounds(w*0.1f, h*0.9f, w*0.8f, -(h*0.8f));
    }

    @Override
    public void act(float delta) {
        this.previewImage.setDrawable(new TextureRegionDrawable(new TextureRegion(editorRoot.getWrappedView())));
        this.previewImage.toFront();
        this.bgImage.toBack();
    }

    @Override
    public boolean blockMainRender() {
        return false;
    }

    @Override
    public boolean wrapMainRender() {
        return true;
    }
}
