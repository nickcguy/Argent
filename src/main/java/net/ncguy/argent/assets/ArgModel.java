package net.ncguy.argent.assets;

import com.badlogic.gdx.graphics.g3d.Model;

/**
 * Created by Guy on 27/07/2016.
 */
public class ArgModel {

    public long id;
    public String name;
    public String g3dbPath;
    public String texturePath;

    private Model model;

    public Model getModel() { return model; }
    public void setModel(Model model) { this.model = model; }

    @Override
    public String toString() {
        return name;
    }
}
