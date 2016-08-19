package net.ncguy.argent.vpl;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import net.ncguy.argent.ui.Icons;

/**
 * Created by Guy on 19/08/2016.
 */
public class VPLNodeRenderable {

    NinePatchDrawable nodeBG;
    NinePatchDrawable nodeSelected;
    Drawable execPin;
    Drawable execPinConnected;
    Drawable pin;
    Drawable pinConnected;
    NinePatchDrawable title;
    NinePatchDrawable titleGloss;
    NinePatchDrawable titleHighlight;

    private static VPLNodeRenderable instance;
    public static VPLNodeRenderable instance() {
        if (instance == null)
            instance = new VPLNodeRenderable();
        return instance;
    }

    private VPLNodeRenderable() {
        nodeBG = new NinePatchDrawable(Icons.Node.NODE_BODY.patch());
        nodeSelected = new NinePatchDrawable(Icons.Node.NODE_SELECTED.patch());
        execPin = Icons.Node.PIN_EXEC.drawable();
        execPinConnected = Icons.Node.PIN_EXEC_CONNECTED.drawable();
        pin = Icons.Node.PIN.drawable();
        pinConnected = Icons.Node.PIN_CONNECTED.drawable();
        title = new NinePatchDrawable(Icons.Node.NODE_TITLE.patch());
        titleGloss = new NinePatchDrawable(Icons.Node.NODE_TITLE_GLOSS.patch());
        titleHighlight = new NinePatchDrawable(Icons.Node.NODE_TITLE_HIGHLIGHT.patch());
    }

    public void render(Batch batch, VPLNode node) {
//        getX(), getY(), getWidth(), getHeight(), headerTable.getHeight()
        render(batch, node.getX(), node.getY(), node.getWidth(), node.getHeight(), node.getHeaderHeight());
    }

    public void render(Batch batch, float x, float y, float w, float h) {
        render(batch, x, y, w, h, 24);
    }

    public void render(Batch batch, float x, float y, float w, float h, float titleHeight) {
        render(batch, x, y, w, h, titleHeight, false);
    }

    public void render(Batch batch, float x, float y, float w, float h, boolean isSelected) {
        render(batch, x, y, w, h, 24, isSelected);
    }

    public void render(Batch batch, float x, float y, float w, float h, float titleHeight, boolean isSelected) {
        if(isSelected) {
            float selX = x - nodeSelected.getLeftWidth();
            float selY = y - nodeSelected.getBottomHeight();
            float selW = w + nodeSelected.getLeftWidth() + nodeSelected.getRightWidth();
            float selH = h + nodeSelected.getBottomHeight() + nodeSelected.getTopHeight();
            nodeSelected.draw(batch, selX, selY, selW, selH);
        }
        nodeBG.draw(batch, x, y, w, h);
        title.draw(batch, x, (y + h) - titleHeight, w, titleHeight);
        titleGloss.draw(batch, x, (y + h) - titleHeight, w, titleHeight);
        titleHighlight.draw(batch, x, (y + h) - titleHeight, w, titleHeight);
    }

    public void renderPin(Batch batch, VPLPin vplPin) {
        Drawable drawable;
        if(vplPin.is(VPLPin.Types.EXEC)) {
            if(vplPin.isConnected())
                drawable = execPinConnected;
            else drawable = execPin;
        }else{
            if(vplPin.isConnected())
                drawable = pinConnected;
            else drawable = pin;
        }
        drawable.draw(batch, vplPin.getX(), vplPin.getY(), vplPin.getWidth(), vplPin.getHeight());
    }
}
