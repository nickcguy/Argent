package net.ncguy.argent.entity;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import net.ncguy.argent.entity.components.ArgentComponent;
import net.ncguy.argent.entity.components.ComponentData;
import net.ncguy.argent.entity.components.model.ModelComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Guy on 15/07/2016.
 */
public class WorldEntity implements RenderableProvider {

    public boolean active = true;
    public String name = "";

    public Vector3 localPosition;
    public Quaternion localRotation;
    public Vector3 localScale;

    public Matrix4 transform;

    List<ArgentComponent> components;


    public WorldEntity() {
        localPosition = new Vector3();
        localRotation = new Quaternion();
        localScale = new Vector3(1, 1, 1);

        transform = new Matrix4(localPosition, localRotation, localScale);
        components = new ArrayList<>();
    }

    @Override
    public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
        transform();
        components.forEach(c -> c.getRenderables(renderables, pool));
    }

    public void update(float delta) {
        components.forEach(c -> c.update(delta));
    }
    public void render(float delta) {
//        components.forEach(c -> c.render(delta));
    }

    @Override
    public String toString() {
        if(name.length() > 0)
            return name;
        return super.toString();
    }

    public Matrix4 transform() {
        transform.set(localPosition, localRotation, localScale);
        return transform;
    }

    public void translate(Vector3 v) {
        localPosition.add(v);
    }

    public void translate(float x, float y, float z) {
        localPosition.add(x, y, z);
    }

    public void rotate(Quaternion q) {
        localRotation.mulLeft(q);
    }

    public void rotate(float x, float y, float z, float w) {
        localRotation.mulLeft(x, y, z, w);
    }

    public void scale(Vector3 v) {
        localScale.scl(v);
    }

    public void scale(float x, float y, float z) {
        localScale.scl(x, y, z);
    }

    public void setLocalPosition(float x, float y, float z) {
        localPosition.set(x, y, z);
    }

    public void setLocalRotation(float x, float y, float z, float w) {
        localRotation.set(x, y, z, w);
    }

    public void setLocalScale(float x, float y, float z) {
        localScale.set(x, y, z);
    }

    public Vector3 getLocalPosition(Vector3 out) {
        return out.set(localPosition);
    }

    public Quaternion getLocalRotation(Quaternion out) {
        return out.set(localRotation);
    }

    public Vector3 getLocalScale(Vector3 out) {
        return out.set(localScale);
    }

    public Vector3 getPosition(Vector3 out) {
        return transform().getTranslation(out);
    }

    public Quaternion getRotation(Quaternion out) {
        return transform().getRotation(out);
    }

    public Vector3 getScale(Vector3 out) {
        return transform().getScale(out);
    }

    public List<ArgentComponent> components() {
        return components;
    }

    public void remove(ArgentComponent component) {
        this.components.remove(component);
    }

    public void add(ArgentComponent component) {
        this.components.add(component);
    }

    public int count(Class<? extends ArgentComponent> componentCls) {
        int count = 0;
        for (ArgentComponent component : this.components)
            if(component.getClass().equals(componentCls)) count++;
        return count;
    }

    public boolean cantApply(Class<? extends ArgentComponent> componentCls) {
        return !canApply(componentCls);
    }
    public boolean canApply(Class<? extends ArgentComponent> componentCls) {
        ComponentData data = componentCls.getAnnotation(ComponentData.class);
        if(data.limit() == -1) return true;
        return count(componentCls) < data.limit();
    }

    public boolean hasNot(Class<? extends ArgentComponent> componentCls) { return !has(componentCls); }
    public boolean has(Class<? extends ArgentComponent> componentCls) {
        return get(componentCls) != null;
    }

    public <T extends ArgentComponent> T get(Class<T> componentCls) {
        for (ArgentComponent component : this.components)
            if(componentCls.isAssignableFrom(component.getClass())) return (T) component;
        return null;
    }

    protected int id = hashCode();
    public void id(int id) { this.id = id; }
    public int id() {
        return id;
    }


    public List<ModelComponent> modelComponents() {
        return components.stream().filter(c -> c instanceof ModelComponent).map(c -> (ModelComponent)c).collect(Collectors.toList());
    }

}
