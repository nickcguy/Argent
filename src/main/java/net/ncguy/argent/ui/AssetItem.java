package net.ncguy.argent.ui;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.PopupMenu;
import net.ncguy.argent.Argent;
import net.ncguy.argent.assets.ArgAsset;
import net.ncguy.argent.ui.dnd.DragZone;

/**
 * Created by Guy on 01/08/2016.
 */
public class AssetItem<T extends ArgAsset> extends DragZone {

    private Label label;
    private T asset;
    private Image icon;
    MutableFloat overlayAlpha;
    Drawable overlay;

    public AssetItem(T asset) {
        super(asset.tag());
        overlayAlpha = new MutableFloat(0);
        setBackground(new NinePatchDrawable(Icons.Node.ASSET_BG.patch()));
        overrideIcon(null);
        align(Align.center);

        Drawable aIcon = asset.icon();
        if(aIcon == null) {
            label = new Label(asset.name(), VisUI.getSkin());
            label.setColor(Color.BLACK);
            label.setWrap(true);
            add(label).fill().expand().row();
        }else{
            icon = new Image(aIcon);
            add(icon).fill().expand().row();
        }

        /*
        label = new Label(asset.name(), VisUI.getSkin());
        label.setWrap(true);

        icon = new Image();
        if(aIcon != null)
            icon.setDrawable(aIcon);
        add(icon).fill().expand().row();
        add(label).fillX().expandX().row();
         */

        this.asset = asset;

        dnd.addSource(new Source(this) {
            @Override
            public Payload dragStart(InputEvent event, float x, float y, int pointer) {
                Payload payload = new Payload();
                payload.setObject(asset);
                payload.setDragActor(new Label(asset.name(), VisUI.getSkin()));

                Label validLbl = new Label(asset.name(), VisUI.getSkin());
                validLbl.setColor(Color.GREEN);
                payload.setValidDragActor(validLbl);

                Label invalidLbl = new Label(asset.name(), VisUI.getSkin());
                invalidLbl.setColor(Color.RED);
                payload.setInvalidDragActor(invalidLbl);

                initDropZones();

                return payload;
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer, Payload payload, DragAndDrop.Target target) {
                super.dragStop(event, x, y, pointer, payload, target);
                targets.forEach(dnd::removeTarget);
                getTargetZones().forEach(z -> z.highlight(false));
                targets.clear();
            }
        });
        initMenu();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if(overlay != null) {
            Color pre = batch.getColor();
            batch.setColor(pre.r, pre.g, pre.b, overlayAlpha.floatValue() * parentAlpha);
            overlay.draw(batch, getX(), getY(), getWidth(), getHeight());
            batch.setColor(pre);
        }
    }

    private void initMenu() {
        PopupMenu menu = asset.contextMenu();
        if(menu == null) return;
        addListener(new ClickListener(Input.Buttons.RIGHT) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Vector2 pos = localToStageCoordinates(new Vector2(x, y));
                menu.showMenu(getStage(), pos.x, pos.y);
                event.stop();
            }
        });
    }

    public T getAsset() {
        return asset;
    }

    public void overrideIcon(Drawable d) {
        if(d == null) {
            tweenOverlayAlpha(0, () -> overlay = d);
        }else{
            overlay = d;
            tweenOverlayAlpha(1);
        }
    }

    public void tweenOverlayAlpha(float target) {
        tweenOverlayAlpha(target, null);
    }

    public void tweenOverlayAlpha(float target, Runnable onFinish) {
        if (Argent.tween == null) {
            overlayAlpha.setValue(target);
            if(onFinish != null) onFinish.run();
        } else {
            Tween.to(overlayAlpha, 0, .4f).target(target).setCallback(((i, baseTween) -> {
                if(i == TweenCallback.COMPLETE && onFinish != null) onFinish.run();
            })).start(Argent.tween);
        }
    }

}
