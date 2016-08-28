package net.ncguy.argent.event.project;

import net.ncguy.argent.editor.project.ProjectContext;
import net.ncguy.argent.event.AbstractEvent;
import net.ncguy.argent.event.Subscribe;

/**
 * Created by Guy on 26/08/2016.
 */
public class NewProjectEvent extends AbstractEvent {

    public ProjectContext context;

    public NewProjectEvent() {}

    public NewProjectEvent(ProjectContext context) { this.context = context; }

    public ProjectContext getContext() { return context; }
    public void setContext(ProjectContext context) { this.context = context; }

    public static interface NewProjectListener {
        @Subscribe
        public void onNewProject(NewProjectEvent event);
    }

}
