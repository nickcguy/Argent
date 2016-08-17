package net.ncguy.argent.event;

/**
 * Created by Guy on 29/07/2016.
 */
public class SceneGraphChangedEvent extends AbstractEvent {

    public static interface SceneGraphChangedListener {
        @Subscribe
        public void onSceneGraphChanged(SceneGraphChangedEvent sceneGraphChangedEvent);
    }

}
