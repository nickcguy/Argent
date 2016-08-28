package net.ncguy.argent.event.project;

import net.ncguy.argent.editor.project.ProjectContext;
import net.ncguy.argent.event.AbstractEvent;
import net.ncguy.argent.event.Subscribe;

/**
 * Created by Guy on 27/08/2016.
 */
public class RemoveProjectEvent extends AbstractEvent {

    public ProjectContext context;

    public RemoveProjectEvent() {}
    public RemoveProjectEvent(ProjectContext context) { this.context = context; }

    public ProjectContext getContext() { return context; }
    public void setContext(ProjectContext context) { this.context = context; }

    public static interface RemoveProjectListener {
        @Subscribe
        public void onRemoveProject(RemoveProjectEvent event);
    }

}
