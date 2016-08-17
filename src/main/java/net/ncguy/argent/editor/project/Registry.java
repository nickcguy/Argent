package net.ncguy.argent.editor.project;


import com.badlogic.gdx.graphics.Texture;
import net.ncguy.argent.Argent;
import net.ncguy.argent.assets.ArgMaterial;
import net.ncguy.argent.assets.ArgTexture;
import net.ncguy.argent.utils.FileUtils;
import net.ncguy.argent.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

    public static final String MATERIAL_EXT = ".mtl";

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
        assert children != null;
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

    public static class FilePathFormats {
        public static final String GLOBAL_MATERIAL = MATERIALS_DIR + "%s" + MATERIAL_EXT;
    }

}
