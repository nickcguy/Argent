package net.ncguy.argent.asset;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.Vector3;
import net.ncguy.argent.Argent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Guy on 05/06/2016.
 */
public class DecalManager {

    public static int MAX_DECALS = 8;
    public boolean billboard = true;

    DecalBatch batch;
    public Camera camera;
    List<Decal> decals;
    Map<Decal, Float> decalLife;

    public DecalManager(Camera camera) {
        this(new DecalBatch(new CameraGroupStrategy(camera)));
        this.camera = camera;
    }

    public DecalManager(CameraGroupStrategy strategy) {
        this(new DecalBatch(strategy));
        camera = strategy.getCamera();
    }

    public DecalManager(DecalBatch batch) {
        this.batch = batch;
        this.decals = new ArrayList<>();
        this.decalLife = new HashMap<>();
    }

    public void addDecal(String string, Vector3 worldPos, Vector3 normal) {
        TextureRegion t = new TextureRegion(Argent.content.get(string, Texture.class));
        addDecal(t, t.getRegionWidth(), t.getRegionHeight(), worldPos, normal);
    }
    public void addDecal(String string, int w, int h, Vector3 worldPos, Vector3 normal) {
        TextureRegion t = new TextureRegion(Argent.content.get(string, Texture.class));
        addDecal(t, w, h, worldPos, normal);
    }

    public void addDecal(TextureRegion texture, int w, int h, Vector3 worldPos, Vector3 normal) {
        addDecal(texture, w, h, worldPos, normal, 5);
    }
    public void addDecal(TextureRegion texture, int w, int h, Vector3 worldPos, Vector3 normal, int life) {
        Decal decal = Decal.newDecal(w, h, texture, true);
        decal.value = life;
        decal.setScale(.1f);
        decal.setPosition(worldPos.add(normal.nor().scl(.001f)));
        decal.lookAt(worldPos.add(normal), Vector3.Zero);
        decals.add(decal);
        decalLife.put(decal, 0f);
    }

    public void toggleBillboard() {
        this.billboard = !this.billboard;
    }

    public void update(float delta) {
        decals.stream().collect(Collectors.toList()).forEach(d -> {
            if(billboard)
                d.lookAt(camera.position, camera.up);
            batch.add(d);
            float life = decalLife.get(d);
            life += delta;
            if(life >= d.value) {
                decals.remove(d);
                decalLife.remove(d);
            }else{
                decalLife.put(d, life);
            }
        });
        batch.flush();
    }

}
