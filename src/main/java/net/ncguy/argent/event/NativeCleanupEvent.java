package net.ncguy.argent.event;

/**
 * Created by Guy on 15/09/2016.
 */
public class NativeCleanupEvent extends AbstractEvent {

    public String identifier;

    public NativeCleanupEvent() {}
    public NativeCleanupEvent(String identifier) { this.identifier = identifier; }

    public static interface NativeCleanupListener {
        @Subscribe
        public void onNativeCleanup(NativeCleanupEvent event);
    }


    public static class Keys {
        public static final String TO_PROJECT_MANAGER = "nativeCleanup.toProjectManager";
    }

}
