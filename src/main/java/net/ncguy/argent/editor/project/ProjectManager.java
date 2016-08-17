package net.ncguy.argent.editor.project;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by Guy on 27/07/2016.
 */
public class ProjectManager implements Disposable {

    public static final String DEFAULT_SCENE_NAME       = "Main Scene";

    public static final String PROJECT_ASSETS_DIR       = "assets/";
    public static final String PROJECT_MODEL_DIR        = PROJECT_ASSETS_DIR + "models/";
    public static final String PROJECT_TERRAIN_DIR      = PROJECT_ASSETS_DIR + "terrain/";

    public static final String PROJECT_TEXTURE_DIR      = PROJECT_ASSETS_DIR + "textures/";
    public static final String PROJECT_SCENES_DIR       = "scenes/";

    public static final String PROJECT_SCENE_EXTENSION  = ".argent";

    private ProjectContext currentProject;
    private final Context context;
    private final Registry registry;

    public ProjectManager() {
        registry = new Registry(this);
        currentProject = new ProjectContext(this);
        context = new Context(this);
    }

    public Registry getRegistry() {
        return registry;
    }

    public Context global() {
        return context;
    }

    public ProjectContext current() {
        return currentProject;
    }

    @Override
    public void dispose() {

    }

    public void newProject(FileHandle file) {

    }

    public void loadProject(FileHandle file) {

    }

    public void saveProject(FileHandle file) {

    }

}
