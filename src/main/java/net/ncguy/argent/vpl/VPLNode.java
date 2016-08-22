package net.ncguy.argent.vpl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.VisUI;
import net.ncguy.argent.utils.ReflectionUtils;
import net.ncguy.argent.vpl.annotations.NodeColour;
import net.ncguy.argent.vpl.struct.IdentifierObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static net.ncguy.argent.vpl.VPLPin.Types.*;

/**
 * Created by Guy on 18/08/2016.
 */
public class VPLNode<T> extends Table {

    protected transient VPLGraph graph;
    protected transient Method method;
    protected boolean continuous;
    protected transient T value;
    protected float width = 28;
    protected transient Table headerTable, inputTable, outputTable;
    protected Set<VPLPin> pinSet;
    protected Vector2 position;
    protected Label titleLabel;
    protected NodeData nodeData;
    protected NodeColour titleColourData;
    public transient Color titleColour;
    protected NodeType type;
    private int index = 0;


    public VPLNode(VPLGraph graph, Method method) {
        super(VisUI.getSkin());
        this.graph = graph;
        this.method = method;
        this.pinSet = new LinkedHashSet<>();
        position = new Vector2();
        buildTable();
        attachInputListener();
        nodeData = method.getAnnotation(NodeData.class);
        titleColourData = nodeData.colour();
        titleColour = VPLManager.instance().getNodeColour(titleColourData);
        discernType();
    }

    private void discernType() {
        Class<?> outType = this.method.getReturnType();
        int inputs = method.getParameterTypes().length - 2;

        if(outType.equals(Void.TYPE)) {
            if(inputs <= 0) {
                this.type = NodeType.EXEC;
            }else{
                this.type = NodeType.FUNCTIONAL;
            }
        }else{
            if(inputs <= 0) {
                this.type = NodeType.DATA;
            }else{
                this.type = NodeType.FUNCTION;
            }
        }

        if(isDefaultColourData(titleColourData))
            titleColour.set(this.type.colour);
    }

    public boolean isDefaultColourData(NodeColour data) {
        if(data.r() < 2) return false;
        if(data.g() < 2) return false;
        if(data.b() < 2) return false;
        if(data.a() < 2) return false;

        return true;
    }

    public Vector2 getPosition() {
        return position;
    }

    public boolean isSelected() {
        return graph.isNodeSelected(this);
    }

    public float getHeaderHeight() {
        return headerTable.getHeight();
    }

    private String getTitle() {
        return VPLManager.instance().getDisplayName(this.method);
    }

    private void buildTable() {
        inputTable = new Table(VisUI.getSkin());
        outputTable = new Table(VisUI.getSkin());

        headerTable = new Table(VisUI.getSkin());
        headerTable.add(titleLabel = new Label(getTitle(), VisUI.getSkin()))
                .padLeft(2).expandX().fillX().left().row();

        buildPins();

        float width = this.width;

        titleLabel.pack();
        float w = titleLabel.getWidth() / 4.5f;
        w *= 1.4f;
        if(w > width) width = w;

        add(headerTable).expandX().fillX().colspan(3).row();
        add(inputTable).left().padLeft(2).minWidth(this.width);
        add("").expand().fill().minWidth(width*2.5f);
        add(outputTable).right().padRight(2).minWidth(this.width);
        pack();

        setTouchable(Touchable.enabled);
    }

    public int index() { return index++; }
    public void resetIndex() { this.index = 0; }

    private void buildPins() {
        resetIndex();
        buildInput();
        resetIndex();
        buildOutput();
        resetIndex();
        assertTables();
    }

    private void buildInput() {
        if(VPLManager.instance().canExecIn(this.method))
            addPin(inputTable, EXEC, INPUT);
        Class<?>[] paramTypes = method.getParameterTypes();
        Parameter[] params = method.getParameters();
        String[] names = new String[params.length-2];
        String[] argNames = VPLManager.instance().getArgNames(this.method);

        for (int i = 0; i < names.length; i++) {
            String name = params[i+2].getName();
            if(i < argNames.length)
                name = argNames[i];
            names[i] = name;
        }

        for (int i = 0; i < names.length; i++)
            addPin(inputTable, paramTypes[i+2], names[i], INPUT);
    }
    private void buildOutput() {
        if(VPLManager.instance().canExecOut(this.method))
            addPin(outputTable, EXEC, OUTPUT);
        Class<?> returnType = method.getReturnType();
        if(returnType.equals(Void.TYPE)) return;
        int pins = VPLManager.instance().getOutPins(method);
        boolean mutable = returnType.equals(IdentifierObject.class);
        int index = 0;
        while (pins > 0) {
            if(mutable)
                returnType = VPLManager.instance().getOutputTypeOfPin(index++, method);
            addPin(outputTable, returnType, COMPOUND, OUTPUT);
            pins--;
        }
    }
    private void assertTables() {
        int inRows = inputTable.getRows();
        int outRows = outputTable.getRows();
        if(inRows == outRows) return;
        if(inRows > outRows) {
            while(inRows > outRows) {
                addElement(outputTable, new Actor());
                outRows = outputTable.getRows();
            }
        }else{
            while(outRows > inRows) {
                addElement(inputTable, new Actor());
                inRows = inputTable.getRows();
            }
        }
    }

    private void attachInputListener() {
        InputListener listener = new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if(button == Input.Buttons.RIGHT) {
                    Vector2 pos = localToStageCoordinates(new Vector2(x, y));
                    graph.nodeContextMenu.getColor().a = 0;
                    graph.nodeContextMenu.showMenu(getStage(), pos.x, pos.y);
                    graph.nodeContextMenu.addAction(Actions.fadeIn(.4f));
                    event.stop();
                    return false;
                }
                if(!Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
                    graph.nodeSelection.clear();
                graph.nodeSelection.choose(VPLNode.this);
                event.stop();
                return true;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                graph.moveSelectedBy();
                super.touchDragged(event, x, y, pointer);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
            }
        };
        addListener(listener);
    }

    private void addPin(Table table, VPLPin.Types... types) {
        addPin(table, new VPLPin(index(), this, types));
    }
    private void addPin(Table table, Class<?> cls, VPLPin.Types... types) {
        addPin(table, cls, cls.getSimpleName(), types);
    }
    private void addPin(Table table, Class<?> cls, String label, VPLPin.Types... types) {
        VPLPin pin = new VPLPin(index(), this, types);
        pin.cls = cls;
        pin.label = label;
        addPin(table, pin);
    }

    private void addPin(Table table, VPLPin pin) {
        pinSet.add(pin);
        addElement(table, pin, pin.label, pin.is(INPUT));
    }
    private void addElement(Table table, Actor actor) {
        addElement(table, actor, true);
    }
    private void addElement(Table table, Actor actor, boolean actorOnLeft) {
        addElement(table, actor, "", actorOnLeft);
    }
    private void addElement(Table table, Actor actor, String label, boolean actorOnLeft) {
        if(actorOnLeft) {
            table.add(actor).left().expandX().fillX().pad(4).size(16);
            table.add(label).left().expandX().fillX().pad(4).row();
        } else{
            table.add(label).left().expandX().fillX().pad(4);
            table.add(actor).left().expandX().fillX().pad(4).size(16).row();
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        VPLNodeRenderable.instance().render(batch, this);
        super.draw(batch, parentAlpha);
        this.position.set(getX(), getY());
    }

    public void invokeSelf(int pin) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        Class<?>[] types = method.getParameterTypes();
        Object[] arg = new Object[types.length];
        arg[0] = this;
        arg[1] = pin;
        for(int i = 2; i < types.length; i++) {
            VPLNode node = getInputNodeAtPin(i-2);
            arg[i] = node.fetchData(this);
        }
        method.invoke(null, arg);

        step();
    }

    private T fetchData(VPLNode node) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        Class<?>[] types = method.getParameterTypes();
        Object[] arg = new Object[types.length];
        arg[0] = this;
        arg[1] = getPinIndex(node);
        for(int i = 2; i < types.length; i++) {
            VPLNode n = getInputNodeAtPin(i-2);
            if(n == node || n == this) {
                arg[i] = ReflectionUtils.newInstance(types[i]);
            }else
                arg[i] = n.fetchData(this);
        }

        if (continuous)
            return (T) method.invoke(null, arg);
        if (value == null) value = (T) method.invoke(null, arg);
        return value;
    }

    private void step() {
        VPLPin execOut = getExecOutPin();
        if(execOut == null) return;
        VPLNode nextNode = execOut.getConnectedNode(0);
        if(nextNode == null) return;
        if(nextNode == this) return;
        try {
            nextNode.invokeSelf(0);
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    public void invalidate() {
        value = null;
    }

    public VPLPin getExecOutPin() {
        List<VPLPin> execOutPins = pinSet.stream().filter(pin -> pin.is(EXEC) && pin.is(OUTPUT)).collect(Collectors.toList());
        if(execOutPins.size() <= 0) return null;
        return execOutPins.get(0);
    }

    public VPLGraph getGraph() { return graph; }

    public VPLNode getInputNodeAtPin(int index) {
        List<VPLPin> pins = pinSet.stream().filter(p -> p.is(INPUT) && !(p.is(EXEC))).collect(Collectors.toList());
        if(pins.size() <= 0) return this;
        return pins.get(MathUtils.clamp(index, 0, pins.size() - 1)).getConnectedNode(0);
    }

    public List<VPLPin> getSidedPins(VPLPin.Types type) {
        return getSidedPins(type.getBit());
    }
    public List<VPLPin> getSidedPins(int mask) {
        return pinSet.stream().filter(p -> p.is(mask) && !(p.is(EXEC))).collect(Collectors.toList());
    }

    public List<VPLPin> getInputPins() {
        return getSidedPins(INPUT);
    }
    public List<VPLPin> getOutputPins() {
        return getSidedPins(OUTPUT);
    }

    public int getPinIndex(VPLNode node) {
        List<VPLPin> pins = getOutputPins();
        for (int i = 0; i < pins.size(); i++) {
            VPLPin pin = pins.get(i);
            if(pin.parentNode.equals(node)) return i;
        }
        return -1;
    }

    //r = .85f, g = 0.24f, b = 0.21f
    public enum NodeType {
        EXEC(new Color(.85f, .24f, .21f, 1f)),
        FUNCTIONAL(new Color(.24f, .21f, .84f, 1f)),
        DATA(new Color(1f, .78f, .28f, 1f)),
        FUNCTION(new Color(.21f, .84f, .24f, 1f)), // TODO rename, too similar to FUNCTIONAL
        ;

        NodeType(Color colour) {
            this.colour = colour;
        }

        public final Color colour;

    }

}
