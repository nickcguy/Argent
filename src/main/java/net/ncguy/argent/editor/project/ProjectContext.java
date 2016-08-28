package net.ncguy.argent.editor.project;

import net.ncguy.argent.project.ProjectMeta;

/**
 * Created by Guy on 27/07/2016.
 */
public class ProjectContext extends Context {

    public ProjectMeta meta;
    public String name;
    public EditorScene currScene;

    @Override
    public String getFilePath() {
        return getMeta().path();
    }

    public ProjectContext(ProjectManager manager) {
        super(manager);
        currScene = new EditorScene();
    }

    public String getName() {
        if(name == null)
            name = "Project";
        return name;
    }

    @Override
    public void copyFrom(Context other) {
        super.copyFrom(other);
        if(other instanceof ProjectContext) {
            ProjectContext pContext = (ProjectContext)other;
            name = pContext.name;
            currScene = pContext.currScene;
        }
    }


    public ProjectMeta getMeta() {
        if(meta == null) {
            meta = new ProjectMeta();
            meta.name = this.name;
            meta.path = this.path;
        }
        return meta;
    }
}
