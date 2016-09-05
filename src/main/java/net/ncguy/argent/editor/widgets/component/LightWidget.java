package net.ncguy.argent.editor.widgets.component;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter;
import com.kotcrab.vis.ui.widget.spinner.SimpleFloatSpinnerModel;
import com.kotcrab.vis.ui.widget.spinner.Spinner;
import com.kotcrab.vis.ui.widget.spinner.SpinnerModel;
import net.ncguy.argent.editor.CommandHistory;
import net.ncguy.argent.editor.project.ProjectContext;
import net.ncguy.argent.editor.project.ProjectManager;
import net.ncguy.argent.editor.widgets.component.commands.*;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.entity.components.LightComponent;
import net.ncguy.argent.ui.ColourPickerWrapper;
import net.ncguy.argent.ui.FloatFieldWithLabel;

import java.util.function.Supplier;

/**
 * Created by Guy on 29/07/2016.
 */
public class LightWidget extends ComponentWidget<LightComponent> {

    private FloatFieldWithLabel posX;
    private FloatFieldWithLabel posY;
    private FloatFieldWithLabel posZ;

    private TextButton colBtn;
    private TextButton ambBtn;
    private TextButton spcBtn;

    private Spinner linear;
    private Spinner quadratic;
    private Spinner intensity;
    private Spinner radius;

    public LightWidget(LightComponent component) {
        super(component, "Light");
        setDeletable(true);
        init();
        setupUI();
        setupListeners();
    }

    protected void init() {
        int size = 65;
        posX = new FloatFieldWithLabel("x", size, true);
        posY = new FloatFieldWithLabel("y", size, true);
        posZ = new FloatFieldWithLabel("z", size, true);
        colBtn = new TextButton("#FFFFFF", VisUI.getSkin());
        ambBtn = new TextButton("#FFFFFF", VisUI.getSkin());
        spcBtn = new TextButton("#FFFFFF", VisUI.getSkin());

        SpinnerModel linearModel = new SimpleFloatSpinnerModel(1, 0.1f, 65536, .1f, 1);
        linear = new Spinner("", linearModel);
        SpinnerModel quadModel = new SimpleFloatSpinnerModel(1, 0.1f, 65536, .1f, 1);
        quadratic = new Spinner("", quadModel);
        SpinnerModel intenModel = new SimpleFloatSpinnerModel(1, 0.1f, 65536, .1f, 1);
        intensity = new Spinner("", intenModel);
        SpinnerModel radModel = new SimpleFloatSpinnerModel(1, 0.1f, 65536, .1f, 1);
        radius = new Spinner("", radModel);
    }
    protected void setupUI() {
        int pad = 4;
        collapsibleContent.add(new VisLabel("Position: ")).padRight(5).padBottom(pad).left();
        collapsibleContent.add(posX).padBottom(pad).padRight(pad);
        collapsibleContent.add(posY).padBottom(pad).padRight(pad);
        collapsibleContent.add(posZ).padBottom(pad).row();

        collapsibleContent.add("Linear: ").padRight(5).padBottom(pad).left();
        collapsibleContent.add(linear).padBottom(pad).colspan(3).expandX().fillX().row();

        collapsibleContent.add("Quadratic: ").padRight(5).padBottom(pad).left();
        collapsibleContent.add(quadratic).padBottom(pad).colspan(3).expandX().fillX().row();

        collapsibleContent.add("Intensity: ").padRight(5).padBottom(pad).left();
        collapsibleContent.add(intensity).padBottom(pad).colspan(3).expandX().fillX().row();

        collapsibleContent.add("Radius: ").padRight(5).padBottom(pad).left();
        collapsibleContent.add(radius).padBottom(pad).colspan(3).expandX().fillX().row();

        collapsibleContent.add("Colour: ").padRight(5).padBottom(pad).left();
        collapsibleContent.add(colBtn).padBottom(pad).colspan(3).expandX().fillX().row();

        collapsibleContent.add("Ambient: ").padRight(5).padBottom(pad).left();
        collapsibleContent.add(ambBtn).padBottom(pad).colspan(3).expandX().fillX().row();

        collapsibleContent.add("Specular: ").padRight(5).padBottom(pad).left();
        collapsibleContent.add(spcBtn).padBottom(pad).colspan(3).expandX().fillX().row();
    }
    protected void setupListeners() {
        posX.addListener(new TranslateListener(projectManager, commandHistory, this::getTranslation));
        posY.addListener(new TranslateListener(projectManager, commandHistory, this::getTranslation));
        posZ.addListener(new TranslateListener(projectManager, commandHistory, this::getTranslation));

        colBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                WorldEntity e = projectManager.current().currScene.selected();
                if(e == null) return;
                LightComponent l = component;
                Color oldColour = new Color(l.getColour());

                ColourPickerWrapper.instance().colour(oldColour);
                ColourPickerWrapper.instance().editAlpha(false);
                ColourPickerWrapper.instance().setListener(new ColorPickerAdapter(){
                    @Override
                    public void canceled(Color oldColor) {
                        colBtn.setText("#"+oldColor.toString());
                        l.getColour().set(oldColor);
                    }

                    @Override
                    public void changed(Color newColor) {
                        colBtn.setText("#"+newColor.toString());
                        l.getColour().set(newColor);
                    }

                    @Override
                    public void finished(Color newColor) {
                        colBtn.setText("#"+newColor.toString());
                        ColourCommand command = new ColourCommand(e);
                        command.setBefore(oldColour);
                        command.setAfter(newColor);
                        command.execute();
                        commandHistory.add(command);
                        ColourPickerWrapper.instance().setListener(null);
                    }
                });
                ColourPickerWrapper.instance().open(colBtn);
            }
        });

        ambBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Color oldColour = component.getColour().cpy();
                ColourPickerWrapper.instance().colour(oldColour);
                ColourPickerWrapper.instance().editAlpha(false);
                ColourPickerWrapper.instance().setListener(new ColorPickerAdapter(){
                    @Override
                    public void canceled(Color oldColor) {
                        ambBtn.setText(oldColor.toString());
                        component.setAmbient(oldColor);
                    }

                    @Override
                    public void changed(Color newColor) {
                        ambBtn.setText(newColor.toString());
                        component.setAmbient(newColor);
                    }

                    @Override
                    public void finished(Color newColor) {
                        ambBtn.setText(newColor.toString());
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
                Color oldColour = component.getColour().cpy();
                ColourPickerWrapper.instance().colour(oldColour);
                ColourPickerWrapper.instance().editAlpha(false);
                ColourPickerWrapper.instance().setListener(new ColorPickerAdapter(){
                    @Override
                    public void canceled(Color oldColor) {
                        spcBtn.setText(oldColor.toString());
                        component.setSpecular(oldColor);
                    }

                    @Override
                    public void changed(Color newColor) {
                        spcBtn.setText(newColor.toString());
                        component.setSpecular(newColor);
                    }

                    @Override
                    public void finished(Color newColor) {
                        spcBtn.setText(newColor.toString());
                        component.setSpecular(newColor);
                        ColourPickerWrapper.instance().setListener(null);
                    }
                });
                ColourPickerWrapper.instance().open(ambBtn);
            }
        });

        linear.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                WorldEntity e = projectManager.current().currScene.selected();
                if(e == null) return;
                LightComponent l = component;
                float val = ((SimpleFloatSpinnerModel)linear.getModel()).getValue();
                LinearCommand cmd = new LinearCommand(e);
                cmd.setBefore(l.getLinear());
                cmd.setAfter(val);
                cmd.execute();
                commandHistory.add(cmd);
            }
        });
        quadratic.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                WorldEntity e = projectManager.current().currScene.selected();
                if(e == null) return;
                if(!e.has(LightComponent.class)) return;
                LightComponent l = e.get(LightComponent.class);
                float val = ((SimpleFloatSpinnerModel)quadratic.getModel()).getValue();
                QuadraticCommand cmd = new QuadraticCommand(e);
                cmd.setBefore(l.getQuadratic());
                cmd.setAfter(val);
                cmd.execute();
                commandHistory.add(cmd);
            }
        });
        intensity.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                WorldEntity e = projectManager.current().currScene.selected();
                if(e == null) return;
                if(!e.has(LightComponent.class)) return;
                LightComponent l = e.get(LightComponent.class);
                float val = ((SimpleFloatSpinnerModel)intensity.getModel()).getValue();
                IntensityCommand cmd = new IntensityCommand(e);
                cmd.setBefore(l.getQuadratic());
                cmd.setAfter(val);
                cmd.execute();
                commandHistory.add(cmd);
            }
        });
        radius.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float val = ((SimpleFloatSpinnerModel)radius.getModel()).getValue();
                component.setRadius(val);
            }
        });
    }

    private Vector3 getTranslation() {
        return new Vector3(posX.getFloat(), posY.getFloat(), posZ.getFloat());
    }


    @Override
    public void setValues(WorldEntity entity) {
        if(!entity.has(LightComponent.class)) return;
        LightComponent light = entity.get(LightComponent.class);
        posX.setText(String.valueOf(light.getLocalPosition().x));
        posY.setText(String.valueOf(light.getLocalPosition().y));
        posZ.setText(String.valueOf(light.getLocalPosition().z));
        colBtn.setText("#"+light.getColour().toString());
        ((SimpleFloatSpinnerModel)linear.getModel()).setValue(light.getLinear());
        ((SimpleFloatSpinnerModel)quadratic.getModel()).setValue(light.getQuadratic());
        ((SimpleFloatSpinnerModel)intensity.getModel()).setValue(light.getIntensity());
        ((SimpleFloatSpinnerModel)radius.getModel()).setValue(light.getRadius());
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
