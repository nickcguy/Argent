package net.ncguy.argent.editor.widgets.component;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.VisUI;
import net.ncguy.argent.editor.project.ProjectContext;
import net.ncguy.argent.editor.widgets.component.commands.BasicCommand;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.entity.components.model.primitive.PrimitiveCubeModelComponent;
import net.ncguy.argent.ui.FloatFieldWithLabel;

import java.util.function.Supplier;

/**
 * Created by Guy on 30/07/2016.
 */
public class PrimitiveCubeModelWidget extends ComponentWidget<PrimitiveCubeModelComponent> {

    private TextField name;
    private FloatFieldWithLabel width;
    private FloatFieldWithLabel height;
    private FloatFieldWithLabel depth;
//    DropZone<ArgShader> mtlDropZone;

    public PrimitiveCubeModelWidget(PrimitiveCubeModelComponent component) {
        super(component, "Cube Primitive");
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
//        mtlDropZone = new DropZone<>(ArgShader.class, "shader");
//
//        mtlDropZone.setOnDrop(component::setMaterial);
    }
    protected void setupUI() {
        int pad = 4;

        collapsibleContent.add("Dimensions: ").padRight(5).padBottom(pad).left();
        collapsibleContent.add(width).padBottom(pad).padRight(pad);
        collapsibleContent.add(height).padBottom(pad).padRight(pad);
        collapsibleContent.add(depth).padBottom(pad).row();

        collapsibleContent.add("Name: ").padRight(5).padBottom(pad).left();
        collapsibleContent.add(name).padBottom(pad).colspan(3).expandX().fillX().row();

//        collapsibleContent.add("Material: ").padRight(5).padBottom(pad).left();
//        collapsibleContent.add(mtlDropZone).padBottom(pad).colspan(3).expandX().fillX().height(64).row();

    }
    protected void setupListeners() {
        name.addListener(new CubeRenameListener(name::getText));

        width.addListener(new CubeResizeListener(this::getDims));
        height.addListener(new CubeResizeListener(this::getDims));
        depth.addListener(new CubeResizeListener(this::getDims));
    }

    protected Vector3 getDims() {
        Vector3 dims = new Vector3();
        dims.x = width.getFloat();
        dims.y = height.getFloat();
        dims.z = depth.getFloat();
        return dims;
    }

    @Override
    public void setValues(WorldEntity entity) {
        this.name.setText(component.name());
        this.width.setText(component.width+"");
        this.height.setText(component.height+"");
        this.depth.setText(component.depth+"");
    }

    public class CubeRenameListener extends ChangeListener {
        final Supplier<String> targetSupplier;

        public CubeRenameListener(Supplier<String> targetSupplier) {
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

    public class CubeResizeListener extends ChangeListener {
        final Supplier<Vector3> targetSupplier;

        public CubeResizeListener(Supplier<Vector3> targetSupplier) {
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

}
