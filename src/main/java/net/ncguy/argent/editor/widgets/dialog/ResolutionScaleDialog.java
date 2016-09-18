package net.ncguy.argent.editor.widgets.dialog;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisDialog;
import net.ncguy.argent.GlobalSettings;
import net.ncguy.argent.event.shader.RefreshFBOEvent;
import net.ncguy.argent.ui.MutableFloatSlider;

/**
 * Created by Guy on 16/09/2016.
 */
public class ResolutionScaleDialog extends VisDialog {

    private static ResolutionScaleDialog instance;
    public static ResolutionScaleDialog instance() {
        if(instance == null)
            instance = new ResolutionScaleDialog();
        return instance;
    }

    TextButton apply;
    TextButton cancel;
    TextButton defaults;

    MutableFloatSlider textureSlider;
    Label textureLabel;

    MutableFloatSlider lightingSlider;
    Label lightingLabel;

    MutableFloatSlider quadSlider;
    Label quadLabel;

    MutableFloatSlider blurSlider;
    Label blurLabel;

    MutableFloatSlider globalSlider;
    Label globalLabel;

    private ResolutionScaleDialog() {
        super("Resolution scale");
        closeOnEscape();
        addCloseButton();
        initUI();
        attachListeners();
    }

    private void initUI() {
        Table root = new Table(VisUI.getSkin());
        root.padTop(6).padRight(6).padBottom(22);
        add(root);

        textureSlider   = new MutableFloatSlider(GlobalSettings.ResolutionScale.texture,  0.1f, 2f, 0.1f, false);
        lightingSlider  = new MutableFloatSlider(GlobalSettings.ResolutionScale.lighting, 0.1f, 2f, 0.1f, false);
        quadSlider      = new MutableFloatSlider(GlobalSettings.ResolutionScale.quad,     0.1f, 2f, 0.1f, false);
        blurSlider      = new MutableFloatSlider(GlobalSettings.ResolutionScale.blur,     0.1f, 2f, 0.1f, false);
        globalSlider    = new MutableFloatSlider(GlobalSettings.ResolutionScale.global,   0.1f, 2f, 0.1f, false);

        textureLabel = new Label("", VisUI.getSkin());
        lightingLabel = new Label("", VisUI.getSkin());
        quadLabel = new Label("", VisUI.getSkin());
        blurLabel = new Label("", VisUI.getSkin());
        globalLabel = new Label("", VisUI.getSkin());

        apply = new TextButton("Apply", VisUI.getSkin());
        cancel = new TextButton("Cancel", VisUI.getSkin());
        defaults = new TextButton("Revert Defaults", VisUI.getSkin());

        root.add("Texture");
        root.add(textureSlider);
        root.add(textureLabel).row();

        root.add("Lighting");
        root.add(lightingSlider);
        root.add(lightingLabel).row();

        root.add("Quad");
        root.add(quadSlider);
        root.add(quadLabel).row();

        root.add("Blur");
        root.add(blurSlider);
        root.add(blurLabel).row();

        root.add("Global");
        root.add(globalSlider);
        root.add(globalLabel).row();

        root.add().colspan(3).height(16).row();

        root.add(apply);
        root.add(defaults);
        root.add(cancel).row();
    }

    private void attachListeners() {
        textureSlider.addListener(new LabelUpdater(textureSlider, textureLabel));
        lightingSlider.addListener(new LabelUpdater(lightingSlider, lightingLabel));
        quadSlider.addListener(new LabelUpdater(quadSlider, quadLabel));
        blurSlider.addListener(new LabelUpdater(blurSlider, blurLabel));
        globalSlider.addListener(new LabelUpdater(globalSlider, globalLabel));

        apply.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                new RefreshFBOEvent().fire();
            }
        });

        cancel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                close();
            }
        });
        defaults.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                textureSlider.setValue(1.0f);
                lightingSlider.setValue(1.0f);
                quadSlider.setValue(1.0f);
                blurSlider.setValue(1.0f);
                globalSlider.setValue(1.0f);
            }
        });
    }

    public static class LabelUpdater extends ChangeListener {

        private final Slider slider;
        private final Label label;

        public LabelUpdater(Slider slider, Label label) {
            this.slider = slider;
            this.label = label;
        }

        public String getText() {
            return (int)(this.slider.getVisualValue()*100f) + "%";
        }

        @Override
        public void changed(ChangeEvent event, Actor actor) {
            this.label.setText(getText());
        }
    }

}
