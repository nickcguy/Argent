package net.ncguy.argent.editor.project;

/**
 * Created by Guy on 27/07/2016.
 */
public class ProjectContext extends Context {


    public String name;
    public EditorScene currScene;

    @Override
    public String getFilePath() {
        return Registry.PROJECTS_DIR + getName() + "/";
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
    public void load() {
        this.mtls.clear();
//        List<ArgMaterial> mtls = projectManager.getRegistry().loadMaterials(getFilePath() + "materials/");
//        mtls.forEach(this.mtls::add);
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



}
