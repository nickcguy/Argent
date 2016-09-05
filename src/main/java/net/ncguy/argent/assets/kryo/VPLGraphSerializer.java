package net.ncguy.argent.assets.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import net.ncguy.argent.vpl.VPLGraph;
import net.ncguy.argent.vpl.VPLNode;
import net.ncguy.argent.vpl.VPLPin;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guy on 01/09/2016.
 */
public class VPLGraphSerializer extends Serializer<VPLGraph> {

    @Override
    public void write(Kryo kryo, Output output, VPLGraph object) {

        // Tags
        int tagCount = object.tags.length;
        kryo.writeObject(output, tagCount);
        for (String tag : object.tags)
            kryo.writeObject(output, tag);

        // Nodes
        int nodeCount = object.nodes.size();
        kryo.writeObject(output, nodeCount);
        for (int i = 0; i < nodeCount; i++)
            kryo.writeObject(output, object.nodes.get(i));

        // Pins
        List<VPLPin> pins = object.getAllConnectedPins();
        int pinCount = pins.size();
        kryo.writeObject(output, pinCount);
        for (int i = 0; i < pinCount; i++)
            kryo.writeObject(output, pins.get(i));

    }

    @Override
    public VPLGraph read(Kryo kryo, Input in, Class<VPLGraph> type) {

        int tagCount = kryo.readObject(in, int.class);
        String[] tags = new String[tagCount];
        for (int i = 0; i < tagCount; i++)
            tags[i] = kryo.readObject(in, String.class);

        VPLGraph graph = new VPLGraph(tags);
        VPLNodeSerializer.graph = graph;
        int nodeCount = kryo.readObject(in, int.class);
        List<VPLNode> nodes = new ArrayList<>();
        for (int i = 0; i < nodeCount; i++)
            nodes.add(kryo.readObject(in, VPLNode.class));

        VPLNodeSerializer.graph = null;

        graph.nodes = nodes;

        int pinCount = kryo.readObject(in, int.class);
        final List<VPLPin> pins = new ArrayList<>();
        for (int i = 0; i < pinCount; i++)
            pins.add(kryo.readObject(in, VPLPin.class));

        pins.forEach(localPinData -> {
            VPLNode<?> localNode = localPinData.getParentNode();
            int localPinId = localPinData.id;
            List<VPLPin> localList;
            if(localPinData.is(VPLPin.Types.INPUT)) localList = localNode.getInputPins_includeExec();
            else localList = localNode.getOutputPins_includeExec();
            VPLPin localPin = localList.get(localPinId);
            localPinData.getConnectedPins().forEach(remotePinData -> {
                VPLNode remoteNode = remotePinData.getParentNode();
                int remotePinId = remotePinData.id;
                List<VPLPin> remoteList;
                if(remotePinData.is(VPLPin.Types.INPUT)) remoteList = remoteNode.getInputPins_includeExec();
                else remoteList = remoteNode.getOutputPins_includeExec();
                VPLPin remotePin = remoteList.get(remotePinId);
                localPin.connect(remotePin);
            });
        });

        graph.updateNodes();

        return graph;
    }
}
