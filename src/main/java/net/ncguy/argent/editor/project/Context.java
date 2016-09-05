package net.ncguy.argent.editor.project;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import net.ncguy.argent.Argent;
import net.ncguy.argent.assets.ArgMaterial;
import net.ncguy.argent.assets.ArgModel;
import net.ncguy.argent.assets.ArgShader;
import net.ncguy.argent.assets.ArgTexture;
import net.ncguy.argent.entity.terrain.Terrain;
import net.ncguy.argent.event.MaterialImportEvent;
import net.ncguy.argent.event.MaterialRefreshEvent;
import net.ncguy.argent.event.shader.NewShaderEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * Created by Guy on 01/08/2016.
 */
public class Context implements Disposable {

    public String path;

    public Array<ArgModel> models;
    public Array<ArgMaterial> mtls;
    public Array<Terrain> terrains;
    public Array<ArgTexture> textures;
    public Array<ArgShader> shaders;

    protected ProjectManager projectManager;

    public String getFilePath() {
        return path;
    }

    public String getContentPath() {
        return getFilePath()+"Content/";
    }

    public Context(ProjectManager projectManager) {
        this.projectManager = projectManager;
        models = new Array<>();
        mtls = new Array<>();
        textures = new Array<>();
        terrains = new Array<>();
        shaders = new Array<>();
    }

    public void load() {
        // Contextual Assets
        Argent.content.clear();
        Argent.content.addDirectoryRoot(getContentPath(), Texture.class, "png", "jpg");
        Argent.content.start();
        while(!Argent.content.update());

        // Materials
        mtls.clear();
        List<ArgMaterial> mtls = projectManager.getRegistry().loadMaterials(getMaterialPath());
        mtls.forEach(this.mtls::add);

        // Textures
        textures.clear();
        List<ArgTexture> texs = projectManager.getRegistry().loadTextures(getTexturePath());
        texs.forEach(this.textures::add);

        shaders.clear();
        List<ArgShader> shdrs = projectManager.getRegistry().loadShaders(getShaderPath());
        shdrs.forEach(this.shaders::add);
    }

    public void copyFrom(Context other) {
        terrains = other.terrains;
        models = other.models;
        mtls = other.mtls;
        textures = other.textures;
    }

    @Override
    public void dispose() {
        for(ArgModel model : models)
            model.getModel().dispose();
        models = null;
    }

    public void addMaterial(ArgMaterial mtl) {
        mtls.add(mtl);
        Argent.event.post(new MaterialImportEvent(mtl));
    }

    public void removeMaterial(ArgMaterial mtl) {
        mtls.removeValue(mtl, false);
    }

    public void refresh() {
        Argent.event.post(new MaterialRefreshEvent());
    }

    public void addMaterial(ArgMaterial mtl, boolean save) {
        addMaterial(mtl);
        if(save) {
            mtl.save(projectManager.getRegistry().getAssetFile(Registry.FilePathFormats.GLOBAL_MATERIAL, mtl.name()));
        }
    }

    public void removeMaterial(ArgMaterial mtl, boolean delete) {
        removeMaterial(mtl);
        if(delete) deleteMaterial(mtl);
    }

    public void deleteMaterial(ArgMaterial mtl) {
        try {
            Files.deleteIfExists(new File(mtl.path()).toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mtl.save(new File(Registry.BIN_DIR+"materials/"+mtl.getFileName()));
    }

    public List<ArgTexture> textures() {
        String path = Registry.TEXTURES_DIR;
        this.textures.clear();
        List<ArgTexture> textures = projectManager.getRegistry().loadTextures(path);
        textures.forEach(this.textures::add);
        return textures;
    }

    public String getMaterialPath() {
        return getContentPath()+"materials/";
    }

    public String getTexturePath() {
        return getContentPath()+"textures/";
    }

    public String getShaderPath() {
        return getContentPath()+"shaders/";
    }

    public void addShader(ArgShader argShader) {
        this.shaders.add(argShader);
        new NewShaderEvent(argShader).fire();
    }
}
