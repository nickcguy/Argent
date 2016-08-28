package net.ncguy.argent.editor.project;


import com.badlogic.gdx.graphics.Texture;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import net.ncguy.argent.Argent;
import net.ncguy.argent.assets.ArgMaterial;
import net.ncguy.argent.assets.ArgTexture;
import net.ncguy.argent.assets.kryo.KryoManager;
import net.ncguy.argent.project.ProjectMeta;
import net.ncguy.argent.utils.FileUtils;
import net.ncguy.argent.utils.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Guy on 01/08/2016.
 */
public class Registry {

    public static final String HOME_DIR = FileUtils.getUserDirectoryPath() + "/" +  ".argent/";

    public static final String PROJECTS_DIR = HOME_DIR + "projects/";
    public static final String ASSETS_DIR = HOME_DIR + "assets/";
    public static final String BIN_DIR = HOME_DIR + "bin/";

    public static final String TEXTURES_DIR = ASSETS_DIR + "textures/";
    public static final String MATERIALS_DIR = ASSETS_DIR + "materials/";

    public static final String FILE_EXT = ".argent";
    public static final String MATERIAL_EXT = ".mtl";

    public static final String REGISTRY_FILE = HOME_DIR + "Registry" + FILE_EXT;

    private static File f = new File(REGISTRY_FILE);

    private ProjectManager projectManager;

    public Registry(ProjectManager projectManager) {
        this.projectManager = projectManager;
    }

    public ArrayList<ArgMaterial> loadMaterials(String path) {
        ArrayList<ArgMaterial> list = new ArrayList<>();
        File file = new File(path);
        if(!file.exists()) {
            file.mkdirs();
            return list;
        }
        File[] children = file.listFiles();
        if(children == null) return list;
        for (File child : children) {
            String ext = FileUtils.getFileExtension(child);
            if(ext.equalsIgnoreCase(MATERIAL_EXT)) {
                try {
                    ArgMaterial mtl = ArgMaterial.load(child);
                    if(mtl == null) continue;
                    list.add(mtl);
                }catch (Exception ignored) {}
            }
        }
        return list;
    }

    public List<ProjectMeta> getProjects() {
        return getRegistryFile().metas;
    }

    public File getAssetFile(String format, String... args) {
        return new File(String.format(format, args));
    }

    public List<ArgTexture> loadTextures(String path) {
        String[] refs = Argent.content.getAllRefs(Texture.class);
        List<ArgTexture> list = new ArrayList<>();
        for (String ref : refs) {
            ArgTexture argTexture = new ArgTexture(Argent.content.get(ref, Texture.class));
            argTexture.name(StringUtils.present(ref.replace("texture_", "")));
            list.add(argTexture);
        }
        return list;
    }

    private RegistryFile getRegistryFile() {
        RegistryFile file = null;

        try {

            file = KryoManager.kryoManager().load(f, RegistryFile.class);
        } catch (FileNotFoundException | KryoException e) {
            e.printStackTrace();
        }

        if(file == null) file = new RegistryFile();
        return file;
    }

    public void addProject(ProjectContext context) {
        registryAction((file) -> file.metas.add(context.getMeta()));
    }

    public void removeProject(ProjectContext context) {
        registryAction((file) -> file.metas.remove(context.getMeta()));
    }

    public void registryAction(Consumer<RegistryFile> action) {
        RegistryFile file = getRegistryFile();
        action.accept(file);
        try {
            KryoManager.kryoManager().save(f, file);
        } catch (FileNotFoundException | KryoException e) {
            e.printStackTrace();
        }
    }

    public static class FilePathFormats {
        public static final String GLOBAL_MATERIAL = MATERIALS_DIR + "%s" + MATERIAL_EXT;
    }

    public static class RegistryFile {

        List<ProjectMeta> metas;

        public RegistryFile() {
            this.metas = new ArrayList<>();
        }
    }

    public static class RegistrySerializer extends Serializer<RegistryFile> {

        @Override
        public void write(Kryo kryo, Output output, RegistryFile object) {
            List<ProjectMeta> metas = object.metas;
            int size = metas.size();
            System.out.println("Size: "+size);
            kryo.writeObject(output, size);
            for (ProjectMeta meta : metas)
                kryo.writeObject(output, meta);
        }

        @Override
        public RegistryFile read(Kryo kryo, Input input, Class<RegistryFile> type) {
            RegistryFile file = new RegistryFile();
            int size = kryo.readObject(input, int.class);
            System.out.println("Size: "+size);
            for (int i = 0; i < size; i++) {
                ProjectMeta meta = kryo.readObject(input, ProjectMeta.class);
                if(meta != null) file.metas.add(meta);
            }

            return file;
        }
    }

}
