package net.ncguy.argent.editor.project;

import com.badlogic.gdx.utils.Disposable;
import net.ncguy.argent.event.project.ContextSelectedEvent;
import net.ncguy.argent.event.project.NewProjectEvent;
import net.ncguy.argent.event.project.RemoveProjectEvent;
import net.ncguy.argent.project.ProjectMeta;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
    private final Registry registry;

    private List<ProjectContext> contexts;

    public ProjectManager() {
        registry = new Registry(this);
        contexts = new ArrayList<>();

        registry.getProjects().forEach(m -> contexts.add(loadContext(m)));

//        currentProject = new ProjectContext(this);
    }

    public ProjectContext getContextFromMeta(ProjectMeta meta) {
        List<ProjectContext> list = contexts.stream().filter(c -> c.getMeta().equals(meta)).collect(Collectors.toList());
        if(list.size() == 0)
            return null;
        return list.get(0);
    }

    public void selectContext(ProjectContext context) {
        currentProject = context;
        context.load();
        new ContextSelectedEvent(context).fire();
    }

    public Registry getRegistry() {
        return registry;
    }

    public ProjectContext current() {
        return currentProject;
    }

    @Override
    public void dispose() {

    }

    public ProjectContext createContext(String name, String path) throws IOException {
        File file = new File(path);
        if(file.exists()) {
            if(!file.isDirectory())
                throw new IOException("File is not a directory: " + file.getAbsolutePath());
            File[] files = file.listFiles();
            if((files != null ? files.length : 0) > 0) {
                System.out.println("Directory not empty, importing instead");
                return importContext(file);
            }
        }else{
            file.mkdirs();
            if(!file.exists()) throw new FileNotFoundException("Unable to create directory: " + file.getAbsolutePath());
        }
        ProjectContext context = new ProjectContext(this);
        context.getMeta().name = name;
        context.getMeta().path = path;
        context.getMeta().created = new Date().toString();
        context.getMeta().lastAccessed = new Date().toString();
        context.getMeta().calculateSize();
        return context;
    }

    public ProjectContext importContext(String path) {
        return importContext(new File(path));
    }
    public ProjectContext importContext(File file) {
        ProjectMeta meta = new ProjectMeta();
        meta.name = file.getName();
        meta.path = file.getAbsolutePath();
        meta.created = "Import: " + new Date().toString();
        meta.lastAccessed = new Date().toString();
        meta.calculateSize();
        return loadContext(meta);
    }

    public void registerContext(String name, String path) {
        try {
            ProjectContext context = createContext(name, path);
            context.getMeta().calculateSize();
            registerContext(context);
            new NewProjectEvent(context).fire();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void registerContext(ProjectContext context) {
        registry.addProject(context);
        contexts.add(context);
        context.getMeta().lastAccessed = new Date().toString();
        context.getMeta().calculateSize();
    }

    public ProjectContext loadContext(ProjectMeta meta) {
        ProjectContext context = new ProjectContext(this);
        context.meta = meta;
        context.name = meta.name;
        context.path = meta.path;
        meta.calculateSize();
        return context;
    }

    public void removeContext(ProjectMeta meta) {
        ProjectContext context = getContextFromMeta(meta);
        if(context != null) {
            contexts.remove(context);
            registry.removeProject(context);
            new RemoveProjectEvent(context).fire();
        }
    }

    public List<ProjectContext> getContexts() {
        return contexts;
    }
}
