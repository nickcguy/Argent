package net.ncguy.argent.network;

/**
 * Created by Guy on 15/06/2016.
 */
public class Replication {

    public enum Event {
        CHANGE,
        TICK,
        INTERVAL,
        ;
    }

    public enum Target {
        SERVER,     // Just the server
        UNICAST,    // Single user
        MULTICAST,  // Multiple endpoints
        BROADCAST,  // Everyone
        ;
    }



}
