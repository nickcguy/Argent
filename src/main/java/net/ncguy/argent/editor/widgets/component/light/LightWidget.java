package net.ncguy.argent.editor.widgets.component.light;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter;
import com.kotcrab.vis.ui.widget.spinner.SimpleFloatSpinnerModel;
import com.kotcrab.vis.ui.widget.spinner.Spinner;
import net.ncguy.argent.editor.CommandHistory;
import net.ncguy.argent.editor.project.ProjectContext;
import net.ncguy.argent.editor.project.ProjectManager;
import net.ncguy.argent.editor.widgets.component.ComponentWidget;
import net.ncguy.argent.editor.widgets.component.commands.ColourCommand;
import net.ncguy.argent.editor.widgets.component.commands.LightTranslateCommand;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.entity.components.light.LightComponent;
import net.ncguy.argent.ui.ColourPickerWrapper;

import java.util.function.Supplier;

/**
 * Created by Guy on 29/07/2016.
 */
public abstract class LightWidget<T extends LightComponent> extends ComponentWidget<T> {

    private TextButton difBtn;
    private TextButton ambBtn;
    private TextButton spcBtn;
    private Spinner intensity;

    private SimpleFloatSpinnerModel intensityModel;

    public LightWidget(T component, String title) {
        super(component, title);
        setDeletable(true);
        init();
        setupUI();
        setupListeners();
    }

    protected void init() {
        difBtn = new TextButton("#FFFFFF", VisUI.getSkin());
        ambBtn = new TextButton("#FFFFFF", VisUI.getSkin());
        spcBtn = new TextButton("#FFFFFF", VisUI.getSkin());
        intensityModel = new SimpleFloatSpinnerModel(1, -1024, 1024, 1, 2);
        intensity = new Spinner("", intensityModel);
    }
    protected void setupUI() {
        int pad = 4;
        collapsibleContent.add("Intensity: ").padRight(5).padBottom(pad).left();
        collapsibleContent.add(intensity).padBottom(pad).growX().row();

        collapsibleContent.add("Colour: ").padRight(5).padBottom(pad).left();
        collapsibleContent.add(difBtn).padBottom(pad).expandX().fillX().row();

        collapsibleContent.add("Ambient: ").padRight(5).padBottom(pad).left();
        collapsibleContent.add(ambBtn).padBottom(pad).expandX().fillX().row();

        collapsibleContent.add("Specular: ").padRight(5).padBottom(pad).left();
        collapsibleContent.add(spcBtn).padBottom(pad).expandX().fillX().row();
    }
    protected void setupListeners() {
        difBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                WorldEntity e = projectManager.current().currScene.selected();
                if(e == null) return;
                LightComponent l = component;
                Color oldColour = new Color(l.getDiffuse());

                ColourPickerWrapper.instance().colour(oldColour);
                ColourPickerWrapper.instance().editAlpha(false);
                ColourPickerWrapper.instance().setListener(new ColorPickerAdapter(){
                    @Override
                    public void canceled(Color oldColor) {
                        difBtn.setText("#"+oldColor.toString());
                        l.getDiffuse().set(oldColor);
                    }

                    @Override
                    public void changed(Color newColor) {
                        difBtn.setText("#"+newColor.toString());
                        l.getDiffuse().set(newColor);
                    }

                    @Override
                    public void finished(Color newColor) {
                        difBtn.setText("#"+newColor.toString());
                        ColourCommand command = new ColourCommand(e);
                        command.setBefore(oldColour);
                        command.setAfter(newColor);
                        command.execute();
                        commandHistory.add(command);
                        ColourPickerWrapper.instance().setListener(null);
                    }
                });
                ColourPickerWrapper.instance().open(difBtn);
            }
        });

        ambBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Color oldColour = component.getDiffuse().cpy();
                ColourPickerWrapper.instance().colour(oldColour);
                ColourPickerWrapper.instance().editAlpha(false);
                ColourPickerWrapper.instance().setListener(new ColorPickerAdapter(){
                    @Override
                    public void canceled(Color oldColor) {
                        ambBtn.setText("#"+oldColor.toString());
                        component.setAmbient(oldColor);
                    }

                    @Override
                    public void changed(Color newColor) {
                        ambBtn.setText("#"+newColor.toString());
                        component.setAmbient(newColor);
                    }

                    @Override
                    public void finished(Color newColor) {
                        ambBtn.setText("#"+newColor.toString());
                        component.setAmbient(newColor);
                        ColourPickerWrapper.instance().setListener(null);
                    }
                });
                ColourPickerWrapper.instance().open(ambBtn);
            }
        });
        spcBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Color oldColour = component.getDiffuse().cpy();
                ColourPickerWrapper.instance().colour(oldColour);
                ColourPickerWrapper.instance().editAlpha(false);
                ColourPickerWrapper.instance().setListener(new ColorPickerAdapter(){
                    @Override
                    public void canceled(Color oldColor) {
                        spcBtn.setText("#"+oldColor.toString());
                        component.setSpecular(oldColor);
                    }

                    @Override
                    public void changed(Color newColor) {
                        spcBtn.setText("#"+newColor.toString());
                        component.setSpecular(newColor);
                    }

                    @Override
                    public void finished(Color newColor) {
                        spcBtn.setText("#"+newColor.toString());
                        component.setSpecular(newColor);
                        ColourPickerWrapper.instance().setListener(null);
                    }
                });
                ColourPickerWrapper.instance().open(ambBtn);
            }
        });
        intensity.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                component.setIntensity(intensityModel.getValue());
            }
        });
    }


    @Override
    public void setValues(WorldEntity entity) {
        difBtn.setText("#"+component.getDiffuse().toString());
        ambBtn.setText("#"+component.getAmbient().toString());
        spcBtn.setText("#"+component.getSpecular().toString());
        intensityModel.setValue(component.getIntensity(), false);
    }

    public static class TranslateListener extends ChangeListener {

        final ProjectManager projectManager;
        final CommandHistory commandHistory;
        final Supplier<Vector3> targetSupplier;

        public TranslateListener(ProjectManager projectManager, CommandHistory commandHistory, Supplier<Vector3> targetSupplier) {
            this.projectManager = projectManager;
            this.commandHistory = commandHistory;
            this.targetSupplier = targetSupplier;
        }

        @Override
        public void changed(ChangeEvent event, Actor actor) {
            final ProjectContext context = projectManager.current();
            buildListener(context, targetSupplier.get());
        }

        protected void buildListener(ProjectContext context, Vector3 offset) {
            WorldEntity e = context.currScene.selected();
            if(e == null) return;
            LightTranslateCommand command = new LightTranslateCommand(e);
            Vector3 pos = e.getLocalPosition(new Vector3());
            command.setBefore(pos);
            command.setAfter(offset);
            command.execute();
            commandHistory.add(command);
        }
    }

}
