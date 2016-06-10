package net.ncguy.argent.vpl.node;

import net.ncguy.argent.utils.TextUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static net.ncguy.argent.vpl.node.VPLNodePin.FLAGS.INPUT;

/**
 * Created by Guy on 09/06/2016.
 */
public class VPLNodePin {

    enum FLAGS {
        INPUT,
        OUTPUT,
        COMPOUND,
        ;
        public int bit() {
            return (int) Math.pow(2, ordinal());
        }
    };

    private int flags;

    private List<VPLNodePin> connectedPins;
    private VPLBaseNode owningNode;

    public VPLNodePin(VPLBaseNode owningNode) {
        this.owningNode = owningNode;
        connectedPins = new ArrayList<>();
    }

    public int setFlags(int flags) {
        return this.flags = flags;
    }

    public int setFlags(FLAGS... flags) {
        this.flags = 0;
        for (FLAGS flag : flags)
            this.flags = this.flags | flag.bit();
        return this.flags;
    }

    public int getFlags() { return this.flags; }

    public boolean hasFlag(FLAGS flag) {
        int len = FLAGS.values().length;
        int index = (len-flag.ordinal())-1;
        char[] flagBits = TextUtils.padBinary(flag.bit(), len).toCharArray();
        char[] flagsBits = TextUtils.padBinary(this.flags, len).toCharArray();
        return flagBits[index] == flagsBits[index];
    }

    public List<VPLNodePin> getValidPins() {
        final boolean selfIsInput = hasFlag(INPUT);
        return connectedPins.stream().filter(p -> {
            boolean isInput = p.hasFlag(INPUT);
            return selfIsInput != isInput;
        }).collect(Collectors.toList());
    }

    public Optional<VPLNodePin> firstValidPin() {
        List<VPLNodePin> valids = getValidPins();
        if(valids.size() >= 1)
            return Optional.of(valids.get(0));
        return Optional.empty();
    }

    public Object getConnectedValue() {
        Optional<VPLNodePin> pin = firstValidPin();
        if(pin.isPresent())
            return pin.get().getNodeValue();
        return null;
    }

    public Object getNodeValue() {
        try {
            return owningNode.invokeFromPin(this);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

}
