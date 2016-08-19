package net.ncguy.argent.vpl;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Guy on 18/08/2016.
 */
public class VPLPin extends Actor {

    public static int splineFidelity = 250;

    enum Types {
        EXEC,
        INPUT,
        OUTPUT,
        ARRAY,
        COMPOUND
        ;
        public int getBit() {
            return (int) Math.pow(2, ordinal());
        }

        public static int Mask = INPUT.getBit() | OUTPUT.getBit() | ARRAY.getBit();
    }

    public boolean is (final long mask) {
        return (mask & typeMask) != 0;
    }
    public boolean is (Types type) { return is(type.getBit()); }

    public void setType(Types... types) {
        int mask = 0;
        for (Types type : types)
            mask |= type.getBit();
        typeMask = mask;
    }

    protected int typeMask;
    protected VPLNode parentNode;
    protected Class<?> cls;
    protected List<VPLPin> connectedpins;

    public VPLPin(Types... types) {
        setType(types);
        init();
    }

    public VPLPin(int typeMask) {
        this.typeMask = typeMask;
        init();
    }

    private void init() {
        connectedpins = new ArrayList<>();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        VPLNodeRenderable.instance().renderPin(batch, this);
    }

    public List<VPLPin> getConnectedpins() {
        return connectedpins;
    }

    public boolean isConnected() {
        return connectedpins.size() > 0;
    }

    public VPLPin getConnectedPin(int index) {
        return connectedpins.get(MathUtils.clamp(index, 0, connectedpins.size() - 1));
    }

    public VPLPin getExecOut() {
        return connectedpins.stream().filter(p -> p.is(Types.EXEC)).collect(Collectors.toList()).get(0);
    }

    public VPLNode getConnectedNode(int index) {
        return getConnectedPin(index).parentNode;
    }
    public VPLNode getExecNode() {
        return getExecOut().parentNode;
    }

}
