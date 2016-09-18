package net.ncguy.argent.event;

/**
 * Created by Guy on 16/08/2016.
 */
public class StringPacketEvent extends AbstractEvent {

    public String key, payload;

    public StringPacketEvent() {
        this("", "");
    }

    public StringPacketEvent(String key) {
        this(key, "");
    }

    public StringPacketEvent(String key, String payload) {
        this.key = key;
        this.payload = payload;
    }

    public StringPacketEvent key(String key) {
        this.key = key;
        return this;
    }

    public StringPacketEvent payload(String payload) {
        this.payload = payload;
        return this;
    }

    public static interface StringPacketListener {
        @Subscribe
        public void onStringPacket(StringPacketEvent event);
    }

}
