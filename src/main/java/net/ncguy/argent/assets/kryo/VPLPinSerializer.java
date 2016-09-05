package net.ncguy.argent.assets.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import net.ncguy.argent.utils.ReflectionUtils;
import net.ncguy.argent.vpl.VPLNode;
import net.ncguy.argent.vpl.VPLPin;

import java.util.List;

/**
 * Created by Guy on 05/09/2016.
 */
public class VPLPinSerializer extends Serializer<VPLPin> {

    @Override
    public void write(Kryo kryo, Output out, VPLPin local) {
        kryo.writeObject(out, local.getParentNode()); // Local parent node
        kryo.writeObject(out, local.id); // Local ID
        kryo.writeObject(out, local.typeMask); // Local type mask
        kryo.writeObject(out, local.cls.getCanonicalName()); // Local Class ref

        // Connections
        List<VPLPin> connectedPins = local.getConnectedPins();
        kryo.writeObject(out, connectedPins.size()); // Remote pin count
        connectedPins.forEach(remote -> {
            kryo.writeObject(out, remote.getParentNode()); // Remote parent node
            kryo.writeObject(out, remote.id); // Remote ID
            kryo.writeObject(out, remote.typeMask); // Remote type mask
            kryo.writeObject(out, remote.cls.getCanonicalName()); // Remote Class ref
        });
    }

    @Override
    public VPLPin read(Kryo kryo, Input in, Class<VPLPin> type) {

        VPLNode localParentNode = kryo.readObject(in, VPLNode.class);
        int localId = kryo.readObject(in, int.class);
        int localTypeMask = kryo.readObject(in, int.class);
        String localClsRef = kryo.readObject(in, String.class);

        VPLPin local = new VPLPin(localId, localParentNode, localTypeMask);
        try {
            local.cls = ReflectionUtils.forName(localClsRef);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Connections
        int remotePinCount = kryo.readObject(in, int.class);
        for (int i = 0; i < remotePinCount; i++) {
            VPLNode remoteParentNode = kryo.readObject(in, VPLNode.class);
            int remoteId = kryo.readObject(in, int.class);
            int remoteTypeMask = kryo.readObject(in, int.class);
            String remoteClsRef = kryo.readObject(in, String.class);
            VPLPin remote = new VPLPin(remoteId, remoteParentNode, remoteTypeMask);
            try {
                remote.cls = ReflectionUtils.forName(remoteClsRef);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            local.connect(remote);
        }

        return local;
    }
}
