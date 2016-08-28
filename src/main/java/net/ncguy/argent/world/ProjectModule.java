package net.ncguy.argent.world;

import net.ncguy.argent.IModule;
import net.ncguy.argent.editor.project.ProjectManager;
import net.ncguy.argent.injector.InjectionStore;

/**
 * Created by Guy on 15/07/2016.
 */
public class ProjectModule extends IModule {

    @Override
    public void init() {
        ProjectManager manager = new ProjectManager();
        try {
            InjectionStore.setGlobal(manager);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String moduleName() {
        return "Project";
    }

}
