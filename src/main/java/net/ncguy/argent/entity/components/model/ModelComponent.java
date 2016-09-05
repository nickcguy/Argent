package net.ncguy.argent.entity.components.model;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import net.ncguy.argent.assets.ArgShader;
import net.ncguy.argent.assets.ArgentShaderProvider;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.entity.components.ArgentComponent;

import static com.badlogic.gdx.graphics.VertexAttributes.Usage.*;
import static net.ncguy.argent.entity.components.ArgentComponent.Type.MODEL;

/**
 * Created by Guy on 29/07/2016.
 */
public abstract class ModelComponent implements ArgentComponent {

    protected static final int attributes = Position | Normal | TextureCoordinates | Tangent | BiNormal;

    WorldEntity entity;
    ModelInstance instance;
    public ArgShader shader;

    String name = "";

    Vector3 localPosition, localScale;
    Quaternion localRotation;

    Matrix4 localTransform;
    Matrix4 worldTransform;

    Renderable instanceRenderable;

    public ModelComponent(WorldEntity entity) {
        this(entity, new ArgShader("", ""));
    }

    public ModelComponent(WorldEntity entity, ArgShader shader) {
        this.entity = entity;
        this.shader = shader;
        this.localPosition = new Vector3(0, 0, 0);
        this.localRotation = new Quaternion();
        this.localScale = new Vector3(1, 1, 1);
        this.localTransform = new Matrix4(localPosition, localRotation, localScale);
        this.instance = new ModelInstance(new Model());
        this.instance.transform = getWorldTransform();

        this.instanceRenderable = new Renderable();

        this.instance.userData = this;
    }

    protected void setModel(Model model) {
        this.instance.materials.clear();

        this.instance.model.nodes.clear();
        this.instance.model.nodes.addAll(model.nodes);

        this.instance.nodes.clear();
        this.instance.nodes.addAll(model.nodes);
    }

    public void setMaterial(ArgShader shader) {
        this.shader = shader;
        Shader s = ArgentShaderProvider.componentShaders.get(this);
        s.dispose();
        ArgentShaderProvider.componentShaders.remove(this);
    }


    @Override
    public WorldEntity getWorldEntity() {
        return this.entity;
    }

    @Override
    public void getRenderables (Array<Renderable> renderables, Pool<Renderable> pool) {
        getWorldTransform();
        instance.getRenderables(renderables, pool);
    }

    public Matrix4 getWorldTransform() {
        if(worldTransform == null)
            worldTransform = new Matrix4();
        localTransform.set(localPosition, localRotation, localScale);
        worldTransform.set(entity.transform().cpy());
        return localTransform.mulLeft(worldTransform);
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public Type getType() {
        return MODEL;
    }

    @Override
    public void setType(Type type) {
        // NOOP
    }

    @Override
    public void remove() {
        entity.remove(this);
    }

    public void name(String name) {
        this.name = name;
    }
    public String name() {
        return this.name;
    }

}
