package net.ncguy.argent.vpl;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.VisUI;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static net.ncguy.argent.vpl.VPLPin.Types.*;

/**
 * Created by Guy on 18/08/2016.
 */
public class VPLNode<T> extends Table {

    protected Method method;
    protected boolean continuous;
    protected T value;
    protected float width = 28;

    protected Table headerTable, inputTable, outputTable;

    public VPLNode(Method method) {
        super(VisUI.getSkin());
        this.method = method;
        buildTable();
    }

    public float getHeaderHeight() {
        return headerTable.getHeight();
    }

    private void buildTable() {
        inputTable = new Table(VisUI.getSkin());
        outputTable = new Table(VisUI.getSkin());
//        inputTable.add(new VPLPin(VPLPin.Types.EXEC, VPLPin.Types.INPUT)).expandX().fillX().left().row();
//        outputTable.add(new VPLPin(VPLPin.Types.EXEC, VPLPin.Types.OUTPUT)).expandX().fillX().right().row();

        headerTable = new Table(VisUI.getSkin());
        headerTable.add(VPLManager.instance().getDisplayName(this.method)).padLeft(2).expandX().fillX().left().row();

        buildPins();

        add(headerTable).expandX().fillX().colspan(3).row();
        add(inputTable).left().padLeft(2).width(width);
        add("").expandX().fillX().width(width*2.5f);
        add(outputTable).right().padRight(2).width(width);
        pack();
    }

    private void buildPins() {
        buildInput();
        buildOutput();
    }

    private void buildInput() {
        if(VPLManager.instance().canExecIn(this.method))
            addNode(inputTable, EXEC, INPUT);
        Class<?>[] paramTypes = method.getParameterTypes();
        for (Class<?> paramType : paramTypes)
            addNode(inputTable, paramType, INPUT);
    }
    private void buildOutput() {
        if(VPLManager.instance().canExecOut(this.method))
            addNode(outputTable, EXEC, OUTPUT);
        Class<?> returnType = method.getReturnType();
        if(returnType.equals(Void.TYPE)) return;
        addNode(outputTable, returnType, COMPOUND, OUTPUT);
    }

    private void addNode(Table table, VPLPin.Types... types) {
        addNode(table, new VPLPin(types));
    }
    private void addNode(Table table, Class<?> cls, VPLPin.Types... types) {
        VPLPin pin = new VPLPin(types);
        pin.cls = cls;
        addNode(table, pin);
    }

    private void addNode(Table table, VPLPin pin) {
        table.add(pin).expandX().fillX().left().padLeft(2).size(24).row();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        setDebug(true, true);
        VPLNodeRenderable.instance().render(batch, this);
        super.draw(batch, parentAlpha);
    }

    public T invoke(int pin, Object... args) throws InvocationTargetException, IllegalAccessException {
        if(continuous)
            return (T) method.invoke(null, args);
        if(value == null) value = (T) method.invoke(null, args);
        return value;
    }

    public void invalidate() {
        value = null;
    }

    public VPLPin getExecOutPin() {
        return null;
    }

}
