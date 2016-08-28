package net.ncguy.argent.event.project;

import net.ncguy.argent.editor.project.ProjectContext;
import net.ncguy.argent.event.AbstractEvent;
import net.ncguy.argent.event.Subscribe;

/**
 * Created by Guy on 27/08/2016.
 */
public class ContextSelectedEvent extends AbstractEvent {

    public ProjectContext context;

    public ContextSelectedEvent() {
    }

    public ContextSelectedEvent(ProjectContext context) {
        this.context = context;
    }

    public static interface ContextSelectedListener {
        @Subscribe
        public void onContextSelected(ContextSelectedEvent event);
    }


}
