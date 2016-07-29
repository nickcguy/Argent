package net.ncguy.argent.render.scene;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.CubemapAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by Guy on 27/07/2016.
 */
public class Skybox implements Disposable {

    private Model boxModel;
    private ModelInstance instance;

    private Cubemap cubemap;
    private FileHandle positiveX;
    private FileHandle negativeX;
    private FileHandle positiveY;
    private FileHandle negativeY;
    private FileHandle positiveZ;
    private FileHandle negativeZ;

    public Skybox(FileHandle positiveX, FileHandle negativeX, FileHandle positiveY, FileHandle negativeY, FileHandle positiveZ, FileHandle negativeZ) {
        set(positiveX, negativeX, positiveY, negativeY, positiveZ, negativeZ);
        instance = new ModelInstance(boxModel = createModel());
    }

    public Model createModel() {
        ModelBuilder mb = new ModelBuilder();
        return mb.createBox(1, 1, 1, new Material(new CubemapAttribute(CubemapAttribute.EnvironmentMap, cubemap)),
                VertexAttributes.Usage.Position);
    }

    public ModelInstance getSkyboxInstance() {
        return instance;
    }

    public void set(FileHandle positiveX, FileHandle negativeX, FileHandle positiveY,
                    FileHandle negativeY, FileHandle positiveZ, FileHandle negativeZ) {
        if(cubemap != null) {
            cubemap.dispose();
        }
        cubemap = new Cubemap(positiveX, negativeX, positiveY, negativeY, positiveZ, negativeZ);
        this.positiveX = positiveX;
        this.negativeX = negativeX;
        this.positiveY = positiveY;
        this.negativeY = negativeY;
        this.positiveZ = positiveZ;
        this.negativeZ = negativeZ;
    }

    public FileHandle getPositiveX() { return positiveX; }
    public FileHandle getNegativeX() { return negativeX; }
    public FileHandle getPositiveY() { return positiveY; }
    public FileHandle getNegativeY() { return negativeY; }
    public FileHandle getPositiveZ() { return positiveZ; }
    public FileHandle getNegativeZ() { return negativeZ; }

    @Override
    public void dispose() {
        boxModel.dispose();
        cubemap.dispose();
    }
}
