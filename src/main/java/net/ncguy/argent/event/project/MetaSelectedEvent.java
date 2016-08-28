package net.ncguy.argent.event.project;

import net.ncguy.argent.event.AbstractEvent;
import net.ncguy.argent.event.Subscribe;
import net.ncguy.argent.project.ProjectMeta;

/**
 * Created by Guy on 27/08/2016.
 */
public class MetaSelectedEvent extends AbstractEvent {

    public ProjectMeta meta;

    public MetaSelectedEvent() {}

    public MetaSelectedEvent(ProjectMeta meta) { this.meta = meta; }

    public static interface MetaSelectedListener {
        @Subscribe
        public void onMetaSelected(MetaSelectedEvent event);
    }

}
