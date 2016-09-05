package net.ncguy.argent.event;

import com.esotericsoftware.minlog.Log;
import net.ncguy.argent.Argent;

/**
 * Created by Guy on 16/08/2016.
 */
public abstract class AbstractEvent {

    public void fire() {
        if(Argent.event == null) {
            System.out.println("Event bus is not enabled");
            return;
        }
        Log.info("[EventBus] >> "+getClass().getSimpleName());
        Argent.event.post(this);
    }

}
