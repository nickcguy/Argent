package net.ncguy.argent.editor.project;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import net.ncguy.argent.assets.ArgModel;
import net.ncguy.argent.assets.ArgTexture;
import net.ncguy.argent.entity.terrain.Terrain;

/**
 * Created by Guy on 27/07/2016.
 */
public class ProjectContext implements Disposable {

    public String name, path;

    public EditorScene currScene;

    public Array<ArgModel> models;
    public Array<Terrain> terrains;
    public Array<ArgTexture> textures;


    public ProjectContext() {
        models = new Array<>();
        textures = new Array<>();
        currScene = new EditorScene();
        terrains = new Array<>();
    }

    public void copyFrom(ProjectContext other) {
        path = other.path;
        name = other.name;
        terrains = other.terrains;
        currScene = other.currScene;
        models = other.models;
        textures = other.textures;
    }

    @Override
    public void dispose() {
        for(ArgModel model : models)
            model.getModel().dispose();
        models = null;
    }
}
