package net.ncguy.argent.editor.widgets.component;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.VisUI;
import net.ncguy.argent.assets.ArgShader;
import net.ncguy.argent.editor.project.ProjectContext;
import net.ncguy.argent.editor.widgets.component.commands.BasicCommand;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.entity.components.model.primitive.PrimitiveSphereModelComponent;
import net.ncguy.argent.ui.FloatFieldWithLabel;
import net.ncguy.argent.ui.dnd.DropZone;
import net.ncguy.argent.utils.StringUtils;

import java.util.function.Supplier;

/**
 * Created by Guy on 04/08/2016.
 */
public class PrimitiveSphereModelWidget extends ComponentWidget<PrimitiveSphereModelComponent> {

    private TextField name;
    private FloatFieldWithLabel width;
    private FloatFieldWithLabel height;
    private FloatFieldWithLabel depth;
    private FloatFieldWithLabel divU;
    private FloatFieldWithLabel divV;
    DropZone<ArgShader> mtlDropZone;

    public PrimitiveSphereModelWidget(PrimitiveSphereModelComponent component) {
        super(component, "Sphere Primitive");
        setDeletable(true);
        init();
        setupUI();
        setupListeners();
    }

    protected void init() {
        int size = 65;
        name = new TextField("", VisUI.getSkin());
        width = new FloatFieldWithLabel("x", size, true);
        height = new FloatFieldWithLabel("y", size, true);
        depth = new FloatFieldWithLabel("z", size, true);
        divU = new FloatFieldWithLabel("u", size, false);
        divV = new FloatFieldWithLabel("v", size, false);
        mtlDropZone = new DropZone<>(ArgShader.class, "shader");

        mtlDropZone.setOnDrop(component::setMaterial);
    }
    protected void setupUI() {
        int pad = 4;

        collapsibleContent.add("Dimensions: ").padRight(5).padBottom(pad).left();
        collapsibleContent.add(width).padBottom(pad).padRight(pad);
        collapsibleContent.add(height).padBottom(pad).padRight(pad);
        collapsibleContent.add(depth).padBottom(pad).row();

        collapsibleContent.add("Divisions: ").padRight(5).padBottom(pad).left();
        collapsibleContent.add(divU).padBottom(pad).padRight(pad);
        collapsibleContent.add(divV).padBottom(pad).row();

        collapsibleContent.add("Name: ").padRight(5).padBottom(pad).left();
        collapsibleContent.add(name).padBottom(pad).colspan(3).expandX().fillX().row();

        collapsibleContent.add("Material: ").padRight(5).padBottom(pad).left();
        collapsibleContent.add(mtlDropZone).padBottom(pad).colspan(3).expandX().fillX().height(64).row();

    }
    protected void setupListeners() {
        name.addListener(new SphereRenameListener(name::getText));

        width.addListener(new SphereResizeListener(this::getDims));
        height.addListener(new SphereResizeListener(this::getDims));
        depth.addListener(new SphereResizeListener(this::getDims));

        divU.addListener(new SphereReshapeListener(this::getDivs));
        divV.addListener(new SphereReshapeListener(this::getDivs));
    }

    protected Vector3 getDims() {
        Vector3 dims = new Vector3();
        dims.x = width.getFloat();
        dims.y = height.getFloat();
        dims.z = depth.getFloat();
        return dims;
    }
    protected Vector2 getDivs() {
        Vector2 divs = new Vector2();
        divs.x = (int)divU.getFloat();
        divs.y = (int)divV.getFloat();
        return divs;
    }

    @Override
    public void setValues(WorldEntity entity) {
        this.name.setText(component.name());
        this.width.setText(StringUtils.formatFloat(component.width, 2));
        this.height.setText(String.valueOf(component.height));
        this.depth.setText(String.valueOf(component.depth));
        this.divU.setText(String.valueOf(component.divU));
        this.divV.setText(String.valueOf(component.divV));
    }

    public class SphereRenameListener extends ChangeListener {
        final Supplier<String> targetSupplier;

        public SphereRenameListener(Supplier<String> targetSupplier) {
            this.targetSupplier = targetSupplier;
        }

        @Override
        public void changed(ChangeEvent event, Actor actor) {
            ProjectContext context = projectManager.current();
            build(context, targetSupplier.get());
        }

        protected void build(ProjectContext context, String target) {
            WorldEntity e = context.currScene.selected();
            if(e == null) return;
            BasicCommand<String> command = new BasicCommand<>(e, (we, t) -> component.name(target));
            command.setBefore(component.name());
            command.setAfter(target);
            command.execute();
            commandHistory.add(command);
        }
    }

    public class SphereResizeListener extends ChangeListener {
        final Supplier<Vector3> targetSupplier;

        public SphereResizeListener(Supplier<Vector3> targetSupplier) {
            this.targetSupplier = targetSupplier;
        }

        @Override
        public void changed(ChangeEvent event, Actor actor) {
            ProjectContext context = projectManager.current();
            build(context, targetSupplier.get());
        }

        protected void build(ProjectContext context, Vector3 target) {
            WorldEntity e = context.currScene.selected();
            if(e == null) return;
            BasicCommand<Vector3> command = new BasicCommand<>(e, (we, t) -> component.set(target));
            command.setBefore(component.get());
            command.setAfter(target);
            command.execute();
            commandHistory.add(command);
        }
    }

    public class SphereReshapeListener extends ChangeListener {
        final Supplier<Vector2> targetSupplier;

        public SphereReshapeListener(Supplier<Vector2> targetSupplier) {
            this.targetSupplier = targetSupplier;
        }

        @Override
        public void changed(ChangeEvent event, Actor actor) {
            ProjectContext context = projectManager.current();
            build(context, targetSupplier.get());
        }

        protected void build(ProjectContext context, Vector2 target) {
            WorldEntity e = context.currScene.selected();
            if(e == null) return;
            BasicCommand<Vector2> command = new BasicCommand<>(e, (we, t) -> component.set(target));
            command.setBefore(component.getDivs());
            command.setAfter(target);
            command.execute();
            commandHistory.add(command);
        }
    }

}
