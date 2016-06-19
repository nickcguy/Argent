package net.ncguy.argent.network.listener;

import com.esotericsoftware.kryonet.*;
import net.ncguy.argent.Argent;

/**
 * Created by Guy on 15/06/2016.
 */
public class ServerListenerFactory {


    public void addToServer(Server server) {
        server.addListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                super.connected(connection);
                EndPoint end = connection.getEndPoint();
                if(end instanceof Client)
                    Argent.network.clientEndPoints.add((Client) end);
            }

            @Override
            public void disconnected(Connection connection) {
                super.disconnected(connection);
                EndPoint end = connection.getEndPoint();
                if(end instanceof Client)
                    Argent.network.clientEndPoints.remove(end);
            }

            @Override
            public void received(Connection connection, Object object) {
                System.out.printf("%s: %s\n", connection.getID(), object.toString());
                server.sendToAllExceptTCP(connection.getID(), object);
            }
        });
    }
}
