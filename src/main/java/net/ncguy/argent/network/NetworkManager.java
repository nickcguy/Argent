package net.ncguy.argent.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.*;
import net.ncguy.argent.core.BasicEntry;
import net.ncguy.argent.network.listener.ServerListenerFactory;
import net.ncguy.argent.observer.IObservable;
import org.reflections.Reflections;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Guy on 15/06/2016.
 */
public class NetworkManager {

    // SERVER
    Server server;
    public List<Client> clientEndPoints;

    public void startServer(short port, ServerListenerFactory listenerFactory) throws IOException {
        this.clientEndPoints = new ArrayList<>();
        server = new Server();
        registerTypes(server);
        server.start();
        server.bind(port, port+1);

        listenerFactory.addToServer(server);
    }

    // CLIENT
    Client client;

    public void startClient(String addr, int port) throws IOException {
        client = new Client();
        registerTypes(client);
        client.start();
        client.connect(5000, addr, port, port+1);

        client.addListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                EndPoint end = connection.getEndPoint();
                if(end instanceof Server)
                    server = (Server) end;
            }

            @Override
            public void disconnected(Connection connection) {
                super.disconnected(connection);
                server = null;
            }
        });
    }

    // SHARED
    public List<Class<?>> kryoTypes = new ArrayList<>();

    public void registerTypes(EndPoint endPoint) {
        initReflectableVars();
        registerTypes(endPoint.getKryo());
    }

    public void registerTypes(Kryo kryo) {
        kryoTypes.forEach(kryo::register);
        kryo.register(String.class);
        kryo.register(Integer.class);
    }

    public void initReflectableVars() {
        Reflections ref = new Reflections();
        Set<Class<?>> classes = ref.getTypesAnnotatedWith(Replicatable.class);
        classes.forEach(kryoTypes::add);
    }

    public void registerInstanceForReflection(Object obj) {
        Class<?> cls = obj.getClass();
        List<Field> tmpFields = new ArrayList<>();
        Collections.addAll(tmpFields, cls.getDeclaredFields());
        List<Field> fields = tmpFields.stream().filter(f -> f.isAnnotationPresent(Replicate.class)).collect(Collectors.toList());
        fields.forEach(f -> {
            f.setAccessible(true);
            try {
                Object fValue = f.get(obj);
                if(fValue instanceof IObservable) {
                    ((IObservable) fValue).addListener((identifier, newVal) -> client.sendTCP(new BasicEntry<>(identifier, newVal)));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

}
