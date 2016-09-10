package net.ncguy.argent.editor.widgets.component;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisLabel;
import net.ncguy.argent.editor.CommandHistory;
import net.ncguy.argent.editor.project.ProjectContext;
import net.ncguy.argent.editor.project.ProjectManager;
import net.ncguy.argent.editor.widgets.component.commands.RotateCommand;
import net.ncguy.argent.editor.widgets.component.commands.ScaleCommand;
import net.ncguy.argent.editor.widgets.component.commands.TranslateCommand;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.ui.FloatFieldWithLabel;
import net.ncguy.argent.utils.StringUtils;

import java.util.function.Supplier;

/**
 * Created by Guy on 27/07/2016.
 */
public class TransformWidget extends BaseInspectorWidget {

    private static final Vector3 tmpV3 = new Vector3();
    private static final Quaternion tmpQuat = new Quaternion();

    private FloatFieldWithLabel posX;
    private FloatFieldWithLabel posY;
    private FloatFieldWithLabel posZ;

    private FloatFieldWithLabel rotX;
    private FloatFieldWithLabel rotY;
    private FloatFieldWithLabel rotZ;

    private FloatFieldWithLabel sclX;
    private FloatFieldWithLabel sclY;
    private FloatFieldWithLabel sclZ;

    public TransformWidget() {
        super("TransformWidget");
        setDeletable(false);
        init();
        setupUI();
        setupListeners();
    }

    protected void init() {
        int size = 65;
        posX = new FloatFieldWithLabel("x", size);
        posY = new FloatFieldWithLabel("y", size);
        posZ = new FloatFieldWithLabel("z", size);
        rotX = new FloatFieldWithLabel("x", size);
        rotY = new FloatFieldWithLabel("y", size);
        rotZ = new FloatFieldWithLabel("z", size);
        sclX = new FloatFieldWithLabel("x", size);
        sclY = new FloatFieldWithLabel("y", size);
        sclZ = new FloatFieldWithLabel("z", size);
    }

    private void setupUI() {
        int pad = 4;
        collapsibleContent.add(new VisLabel("Position: ")).padRight(5).padBottom(pad).left();
        collapsibleContent.add(posX).padBottom(pad).padRight(pad);
        collapsibleContent.add(posY).padBottom(pad).padRight(pad);
        collapsibleContent.add(posZ).padBottom(pad).row();

        collapsibleContent.add(new VisLabel("Rotation: ")).padRight(5).padBottom(pad).left();
        collapsibleContent.add(rotX).padBottom(pad).padRight(pad);
        collapsibleContent.add(rotY).padBottom(pad).padRight(pad);
        collapsibleContent.add(rotZ).padBottom(pad).row();

        collapsibleContent.add(new VisLabel("Scale: ")).padRight(5).padBottom(pad).left();
        collapsibleContent.add(sclX).padBottom(pad).padRight(pad);
        collapsibleContent.add(sclY).padBottom(pad).padRight(pad);
        collapsibleContent.add(sclZ).padBottom(pad).row();
    }

    private void setupListeners() {
        posX.addListener(new TranslateListener(projectManager, commandHistory, this::getTranslation));
        posY.addListener(new TranslateListener(projectManager, commandHistory, this::getTranslation));
        posZ.addListener(new TranslateListener(projectManager, commandHistory, this::getTranslation));

        rotX.addListener(new RotateListener(projectManager, commandHistory, this::getQuaternion));
        rotY.addListener(new RotateListener(projectManager, commandHistory, this::getQuaternion));
        rotZ.addListener(new RotateListener(projectManager, commandHistory, this::getQuaternion));

        sclX.addListener(new ScaleListener(projectManager, commandHistory, this::getScale));
        sclY.addListener(new ScaleListener(projectManager, commandHistory, this::getScale));
        sclZ.addListener(new ScaleListener(projectManager, commandHistory, this::getScale));
    }

    private Vector3 getTranslation() {
        return new Vector3(posX.getFloat(), posY.getFloat(), posZ.getFloat());
    }

    private Quaternion getQuaternion() {
        Quaternion quat = new Quaternion();
        quat.setEulerAngles(rotX.getFloat(), rotY.getFloat(), rotZ.getFloat());
        return quat;
    }

    private Vector3 getScale() {
        return new Vector3(sclX.getFloat(), sclY.getFloat(), sclZ.getFloat());
    }

    @Override
    public void onDelete() {

    }

    @Override
    public void setValues(WorldEntity e) {

        int cursorposX = posX.getField().getCursorPosition();
        int cursorposY = posY.getField().getCursorPosition();
        int cursorposZ = posZ.getField().getCursorPosition();
        int cursorrotX = rotX.getField().getCursorPosition();
        int cursorrotY = rotY.getField().getCursorPosition();
        int cursorrotZ = rotZ.getField().getCursorPosition();
        int cursorsclX = sclX.getField().getCursorPosition();
        int cursorsclY = sclY.getField().getCursorPosition();
        int cursorsclZ = sclZ.getField().getCursorPosition();

        boolean valid = e != null;
        posX.setEditable(valid);
        posY.setEditable(valid);
        posZ.setEditable(valid);
        rotX.setEditable(valid);
        rotY.setEditable(valid);
        rotZ.setEditable(valid);
        sclX.setEditable(valid);
        sclY.setEditable(valid);
        sclZ.setEditable(valid);
        if(!valid) return;

        Vector3 pos = e.getLocalPosition(tmpV3);
        posX.setText(StringUtils.formatFloat(pos.x, 2));
        posY.setText(StringUtils.formatFloat(pos.y, 2));
        posZ.setText(StringUtils.formatFloat(pos.z, 2));

        Quaternion rot = e.getLocalRotation(tmpQuat);
        rotX.setText(StringUtils.formatFloat(rot.getPitch(), 2));
        rotY.setText(StringUtils.formatFloat(rot.getYaw(), 2));
        rotZ.setText(StringUtils.formatFloat(rot.getRoll(), 2));

        Vector3 scl = e.getLocalScale(tmpV3);
        sclX.setText(StringUtils.formatFloat(scl.x, 2));
        sclY.setText(StringUtils.formatFloat(scl.y, 2));
        sclZ.setText(StringUtils.formatFloat(scl.z, 2));

        posX.getField().setCursorPosition(cursorposX);
        posY.getField().setCursorPosition(cursorposY);
        posZ.getField().setCursorPosition(cursorposZ);
        rotX.getField().setCursorPosition(cursorrotX);
        rotY.getField().setCursorPosition(cursorrotY);
        rotZ.getField().setCursorPosition(cursorrotZ);
        sclX.getField().setCursorPosition(cursorsclX);
        sclY.getField().setCursorPosition(cursorsclY);
        sclZ.getField().setCursorPosition(cursorsclZ);
    }

    public static abstract class TransformListener<T> extends ChangeListener {
        final ProjectManager projectManager;
        final CommandHistory commandHistory;
        final Supplier<T> targetSupplier;

        public TransformListener(ProjectManager projectManager, CommandHistory commandHistory, Supplier<T> targetSupplier) {
            this.projectManager = projectManager;
            this.commandHistory = commandHistory;
            this.targetSupplier = targetSupplier;
        }

        @Override
        public void changed(ChangeEvent event, Actor actor) {
            final ProjectContext context = projectManager.current();
            buildListener(context, targetSupplier.get());
        }

        protected abstract void buildListener(ProjectContext context, T target);
    }

    public static class TranslateListener extends TransformListener<Vector3> {
        public TranslateListener(ProjectManager projectManager, CommandHistory commandHistory, Supplier<Vector3> targetSupplier) {
            super(projectManager, commandHistory, targetSupplier);
        }

        protected void buildListener(ProjectContext context, Vector3 offset) {
            WorldEntity e = context.currScene.selected();
            if(e == null) return;
            TranslateCommand command = new TranslateCommand(e);
            Vector3 pos = e.getLocalPosition(tmpV3);
            command.setBefore(pos);
            command.setAfter(offset);
            command.execute();
            commandHistory.add(command);
        }
    }

    public static class RotateListener extends TransformListener<Quaternion> {

        public RotateListener(ProjectManager projectManager, CommandHistory commandHistory, Supplier<Quaternion> targetSupplier) {
            super(projectManager, commandHistory, targetSupplier);
        }

        @Override
        protected void buildListener(ProjectContext context, Quaternion target) {
            WorldEntity e = context.currScene.selected();
            if(e == null) return;
            RotateCommand command = new RotateCommand(e);
            Quaternion quat = e.getLocalRotation(tmpQuat);
            command.setBefore(quat);
            command.setAfter(target);
            command.execute();
            commandHistory.add(command);
        }
    }

    public static class ScaleListener extends TransformListener<Vector3> {

        public ScaleListener(ProjectManager projectManager, CommandHistory commandHistory, Supplier<Vector3> targetSupplier) {
            super(projectManager, commandHistory, targetSupplier);
        }

        @Override
        protected void buildListener(ProjectContext context, Vector3 target) {
            WorldEntity e = context.currScene.selected();
            if(e == null) return;
            ScaleCommand command = new ScaleCommand(e);
            Vector3 scl = e.getLocalScale(tmpV3);
            command.setBefore(scl);
            command.setAfter(target);
            command.execute();
            commandHistory.add(command);
        }
    }
}
