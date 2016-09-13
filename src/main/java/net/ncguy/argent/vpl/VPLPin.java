package net.ncguy.argent.vpl;

import aurelienribon.tweenengine.Tween;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.kotcrab.vis.ui.VisUI;
import net.ncguy.argent.Argent;
import net.ncguy.argent.event.StringPacketEvent;
import net.ncguy.argent.ui.dnd.DragDropZone;
import net.ncguy.argent.utils.AppUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static net.ncguy.argent.tween.accessor.ColorTweenAccessor.*;
import static net.ncguy.argent.vpl.VPLPin.Types.*;

/**
 * Created by Guy on 18/08/2016.
 */
public class VPLPin extends Group {

    public static int splineFidelity = 250;
    private Vector2 position = new Vector2();
    protected List<VPLPinListener> listeners;

    public enum Types {
        EXEC,
        INPUT,
        OUTPUT,
        ARRAY,
        COMPOUND
        ;
        public int getBit() {
            return (int) Math.pow(2, ordinal());
        }

    }

    public boolean is (final long mask) {
        return (mask & typeMask) != 0;
    }
    public boolean is (Types type) { return is(type.getBit()); }

    public String canConnect_D(VPLPin pin) {

        if(pin.parentNode == this.parentNode) return "Same parent";

        if(match(pin, INPUT)) return "Both inputs";
        if(match(pin, OUTPUT)) return "Both outputs";
        if(xorMatch(pin, EXEC)) return "Not both execs";
        boolean thisAtCapacity = this.atCapacity();
        boolean pinAtCapacity = pin.atCapacity();

        if(thisAtCapacity || pinAtCapacity) return "At capacity";
        if(xorMatch(pin, ARRAY)) return "Not both arrays";

        if(this.is(EXEC) && pin.is(EXEC)) return "";

        if(this.cls.equals(Object.class) || pin.cls.equals(Object.class)) return "";

        if(!(this.cls.equals(pin.cls))) return String.format("Invalid type conversion >> %s -> %s", this.cls.getSimpleName(), pin.cls.getSimpleName());

        return "";
    }

    public boolean canConnect(VPLPin pin) {
        String canConnect_D = canConnect_D(pin);
        if(canConnect_D.length() == 0)
            return true;
        if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
            if("At capacity".equalsIgnoreCase(canConnect_D)) {
                List<VPLPin> temp = this.connectedPins.stream().collect(Collectors.toList());
                Gdx.app.postRunnable(() -> temp.forEach(this::disconnect));
                return true;
            }
        }
        new StringPacketEvent("toast|error", canConnect_D).fire();
//        parentNode.graph.toaster.error(canConnect_D);
//        System.out.println("VPLPin.canConnect >> "+canConnect_D);

        return false;
    }

    public boolean atCapacity() {
        if(this.is(COMPOUND))
            return false;
        return this.connectedPins.size() > 0;
    }

    public boolean xorMatch(VPLPin pin, Types type) {
        return (is(type) && !pin.is(type)) || (pin.is(type) && !is(type));
    }

    public boolean match(VPLPin pin, Types type) {
        return this.is(type) && pin.is(type);
    }

    public void setType(Types... types) {
        int mask = 0;
        for (Types type : types)
            mask |= type.getBit();
        typeMask = mask;
    }

    public int id;
    public int typeMask;
    protected VPLNode parentNode;
    public Class<?> cls;
    protected List<VPLPin> connectedPins;
    protected DragDropZone<VPLPin> zone;
    protected boolean renderDraggedSpline = false;
    protected Color selfColour;
    protected Color colour;
    protected Tween colourTween;
    protected String label;
    protected boolean useNativeColour = true;

    public VPLPin(int id, VPLNode parentNode, Types... types) {
        this.id = id;
        this.parentNode = parentNode;
        setType(types);
        init();
    }

    public VPLPin(int id, VPLNode parentNode, int typeMask) {
        this.id = id;
        this.parentNode = parentNode;
        this.typeMask = typeMask;
        init();
    }

    public VPLNode getParentNode() {
        return parentNode;
    }

    public void connect(VPLPin pin) {
        boolean canConnect = canConnect(pin);
        if(!canConnect) return;
        forceConnect(pin);
    }

    public void forceConnect(VPLPin pin) {
        if(connectedPins.contains(pin)) return;
        connectedPins.add(pin);
        pin.forceConnect(this);

        listeners.forEach(l -> l.connected(pin));
    }

    public void disconnect(VPLPin p) {
        if(!connectedPins.contains(p)) return;
        connectedPins.remove(p);
        p.disconnect(this);

        listeners.forEach(l -> l.disconnected(p));
    }

    public void disconnectAll() {
        connectedPins.stream().collect(Collectors.toList()).forEach(this::disconnect);
        connectedPins.clear();
    }

    private void init() {
        listeners = new ArrayList<>();
        connectedPins = new ArrayList<>();
        zone = new DragDropZone<>("pin", VPLPin.class, "pin");
        zone.dnd().setTapSquareSize(.1f);
        zone.dnd().addSource(new DragAndDrop.Source(this) {
            @Override
            public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                parentNode.graph.draggingPin = true;
                DragAndDrop.Payload payload = new DragAndDrop.Payload();
                payload.setObject(VPLPin.this);
                renderDraggedSpline = true;
                zone.initDropZones();

                payload.setDragActor(new Label("Dragging", VisUI.getSkin()));
                payload.setValidDragActor(new Label("Dragging - Valid", VisUI.getSkin()));
                payload.setInvalidDragActor(new Label("Dragging - Invalid", VisUI.getSkin()));

                return payload;
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
                parentNode.graph.draggingPin = false;
                renderDraggedSpline = false;
                zone.targets.forEach(zone.dnd()::removeTarget);
                zone.getTargetZones().forEach(z -> z.highlight(false));
                zone.targets.clear();
            }
        });
        addActor(zone);
        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if(button == Input.Buttons.RIGHT) {
                    if(AppUtils.Input.isShiftPressed())
                        disconnectAll();
                }
                event.stop();
                return false;
            }
        });
        zone.setFillParent(true);

        zone.setOnDrop(pin -> {
            System.out.println("VPLPin.init >> "+pin.toString());
            this.connect(pin);
        });
        zone.setOnHover(this::onHover);
        zone.setOnReset(this::onReset);
        selfColour = VPLManager.instance().getPinColour(this).cpy();
        colour = selfColour.cpy();
    }

    public void onHover(DragDropZone.TargetDragPayload drag) {
        Object obj = drag.payload.getObject();
        if(obj instanceof VPLPin) {
            VPLPin that = (VPLPin)obj;
            Color thisColour = this.getColour();
            that.tweenColour(thisColour);
        }
    }
    public void onReset(DragDropZone.TargetResetPayload reset) {
        Object obj = reset.payload.getObject();
        if(obj instanceof VPLPin) {
            VPLPin that = (VPLPin)obj;
            that.tweenColour(that.getColour());
        }
    }

    public void renderDraggedSpline(Batch batch) {
        if(!renderDraggedSpline) return;
        Vector2[] points = VPLNodeRenderable.instance().defaultPoints(getNormalPosition(), new Vector2(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY()));
        VPLNodeRenderable.instance().renderTweenSpline(batch, splineFidelity, getColour(), this.colour, points);
    }

    public VPLPin useNativeColour(boolean use) {
        this.useNativeColour = use;
        return this;
    }
    public boolean useNativeColour() {
        return this.useNativeColour;
    }

    public Color getColour() {
        if(useNativeColour)
            this.selfColour.set(VPLManager.instance().getPinColour(this));
        return this.selfColour;
    }

    public void setColour(Color colour) {
        this.colour.set(colour);
    }
    public void resetColour() {
        tweenColour(Color.WHITE);
    }

    public void tweenColour(Color colour) {
        Tween.to(this.colour, RED | GREEN | BLUE, 1f).target(colour.r, colour.g, colour.b).start(Argent.tween);
    }

    public Vector2 getPosition() {
        return position.set(getX(), getY());
    }

    public Vector2 getNormalPosition() {
        Vector2 norm = getPosition().cpy();
        norm = localToStageCoordinates(norm);
        norm.sub(getPosition());
//        VPLPane pane = parentNode.graph.pane;
//        norm.add(pane.getX(), pane.getY());
        norm.add(getWidth()/2, getHeight()*.5f);
        return norm;
    }

    private static Vector2 normalise(Actor actor, Vector2 local) {
        System.out.printf("%s: [%s, %s]\n", actor.getClass().getSimpleName(), actor.getX(), actor.getY());
        local.add(actor.getX(), actor.getY());
        if(actor.hasParent())
            normalise(actor.getParent(), local);
        return local;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
//        setDebug(false, true);
        batch.setColor(getColour());
        VPLNodeRenderable.instance().renderPin(batch, this);
        renderDraggedSpline(batch);
        batch.setColor(Color.WHITE);
        super.draw(batch, parentAlpha);
    }

    public List<VPLPin> getConnectedPins() {
        return connectedPins;
    }

    public boolean isConnected() {
        return connectedPins.size() > 0;
    }

    public VPLPin getConnectedPin(int index) {
        if(connectedPins.size() == 0) return this;
        return connectedPins.get(MathUtils.clamp(index, 0, connectedPins.size() - 1));
    }

    public VPLPin getExecOut() {
        return connectedPins.stream().filter(p -> p.is(EXEC)).collect(Collectors.toList()).get(0);
    }

    public VPLNode getConnectedNode(int index) {
        return getConnectedPin(index).parentNode;
    }
    public VPLNode getExecNode() {
        return getExecOut().parentNode;
    }

    public void invokeNode() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        parentNode.invokeSelf(this.id);
    }

    public void addPinListener(VPLPinListener listener) {
        listeners.add(listener);
    }
    public void removePinListener(VPLPinListener listener) {
        listeners.remove(listener);
    }

}
