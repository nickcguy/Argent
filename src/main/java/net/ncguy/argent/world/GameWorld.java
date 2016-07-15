package net.ncguy.argent.world;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.dynamics.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.strongjoshua.console.Console;
import com.strongjoshua.console.GUIConsole;
import net.ncguy.argent.Argent;
import net.ncguy.argent.command.CommandProcessor;
import net.ncguy.argent.core.VarRunnables;
import net.ncguy.argent.physics.debug.DebugRenderer;
import net.ncguy.argent.render.WorldRenderer;
import net.ncguy.argent.ui.BufferWidget;
import net.ncguy.argent.world.landscape.Terrain;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Guy on 13/06/2016.
 */
public class GameWorld {
    public static class Generic<T> implements Disposable {

        public Terrain terrain;
        protected transient WorldRenderer<T> renderer;
        protected List<T> instances;
        protected transient Console console;
        protected transient T selected;

        public transient Skin consoleSkin;
        public transient boolean consoleEnabled = false;
        public transient boolean showBuffers = false;
        public transient Runnable onSave, onLoad;

        protected transient List<VarRunnables.VarRunnable<T>> onSelect = new ArrayList<>();

        public Generic(WorldRenderer<T> renderer, List<T> instances) {
            this.instances = instances;
            this.renderer = renderer;
        }

        public T selected() {
            if(selected == null) {
                if(instances.size() > 0) select(instances.get(0));
                else select(null);
            }
            return selected;
        }
        public Generic select(T selected) {
            this.selected = selected;
            this.onSelect.forEach(s -> s.run(selected));
            return this;
        }

        public void onSave() { if(onSave != null) onSave.run(); }
        public void onLoad() { if(onLoad != null) onLoad.run(); }

        public void addOnSelect(VarRunnables.VarRunnable<T> onSelect) { this.onSelect.add(onSelect); }
        public void removeOnSelect(VarRunnables.VarRunnable<T> onSelect) { this.onSelect.remove(onSelect); }


        public void addLandscape(Vector3 origin, int width, int height, int cellWidth, int cellHeight) {
            if(terrain != null) return;
            terrain = new Terrain(64);
            terrain.init();
            renderer().addDirectInstance(terrain);
//            if(landscape != null) return;
//            landscape = new LandscapeWorldActor(this, origin, width, height, cellWidth, cellHeight);
//            renderer().addDirectInstance(landscape.instance());
        }

        public boolean hasLandscape() {
            return this.terrain != null;
        }

        public void addInstance(T instance) {
            if(this.containsInstance(instance))
                this.instances.remove(instance);
            this.instances.add(instance);
        }
        public void removeInstance(T instance) {
            this.instances.remove(instance);
        }

        public List<T> instances() { return instances; }

        public void initConsole() {
            if (!consoleEnabled) return;
            if (consoleSkin == null) this.console = new GUIConsole();
            else this.console = new GUIConsole(consoleSkin);

            try {
                Field f = GUIConsole.class.getDeclaredField("stage");
                if (f != null) {
                    f.setAccessible(true);
                    Stage s = (Stage) f.get(this.console);
                    Argent.onResize.add((w, h) -> {
                        s.getViewport().update(w, h, true);
                        s.getCamera().viewportWidth = w;
                        s.getCamera().viewportHeight = h;
                        s.getCamera().update(true);
                    });
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }


            this.console.setSizePercent(100, 33);
            this.console.setPositionPercent(0, 67);
            this.console.setKeyID(Input.Keys.GRAVE);

            this.console.setCommandExecutor(new CommandProcessor(this, this.console));
        }

        public void renderConsole() {
            if (!consoleEnabled) return;
            if (this.console == null) {
                initConsole();
                return;
            }
            console.draw();
        }

        public void render(float delta) {
            renderer.render(delta);
            renderConsole();
        }

        public void updateBuffers(Stage stage, Table container, List<BufferWidget> widgets, Skin skin) {

        }



        public Console console() {
            return console;
        }

        public WorldRenderer<T> renderer() {
            return renderer;
        }

        @Override public void dispose() {}

        public void clear() {
            selected = null;
            renderer().clearRenderPipe();
            instances.stream().collect(Collectors.toList()).forEach(this::removeInstance);
        }

        public boolean containsInstance(T obj) {
            return this.instances.contains(obj);
        }
    }

    public static class Physics<T> extends Generic<T> {

        public enum WorldFlags {
            FLAG0(0),
            FLAG1(1),
            FLAG2(2),
            FLAG3(4),
            FLAG4(8),
            FLAG5(16),
            FLAG6(32),
            FLAG7(64),
            FLAG8(128),
            FLAG9(256),
            FLAG10(512),
            FLAG11(1024),
            FLAG12(2048),
            FLAG13(4096),
            FLAG14(8192),
            FLAG15(16384),
            ALL(Short.MAX_VALUE),
            ;
            WorldFlags(int i) { this.flag = (short)i;}
            WorldFlags(short i) {
                this.flag = i;
            }
            public short flag;
        }

        public transient btCollisionConfiguration configuration;
        public transient btCollisionDispatcher dispatcher;
        public transient btDbvtBroadphase broadphase;
        public transient btConstraintSolver constraintSolver;
        public transient btDynamicsWorld dynamicsWorld;

        private transient DebugRenderer debugRenderer;

        public Physics(WorldRenderer<T> renderer, List<T> instances) {
            super(renderer, instances);
            initWorld();
        }

        public void initWorld() {
            configuration = new btDefaultCollisionConfiguration();
            dispatcher = new btCollisionDispatcher(configuration);
            broadphase = new btDbvtBroadphase();
            constraintSolver = new btSequentialImpulseConstraintSolver();
            dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, constraintSolver, configuration);
            dynamicsWorld.setGravity(new Vector3(0, -10, 0));

            debugRenderer = new DebugRenderer(dynamicsWorld);

        }

        @Override
        public void addInstance(T instance) {
            btRigidBody body = renderer.getBulletBody(instance);
            if(body != null) {
                if(!instances().contains(instance)) {
                    super.addInstance(instance);
                    dynamicsWorld.addRigidBody(body, WorldFlags.FLAG0.flag, WorldFlags.ALL.flag);
                }
            }
        }


        @Override
        public void removeInstance(T instance) {
            btRigidBody body = renderer.getBulletBody(instance);
            if(body != null) {
                if(instances().contains(instance)) {
                    super.removeInstance(instance);
                    dynamicsWorld.removeRigidBody(body);
                }
            }
        }

        @Override
        public void render(float delta) {
            dynamicsWorld.setGravity(new Vector3(0, -100, 0));
            // Restricts physics update to a maximum of 5 per frame.
            // Should only do 1 though, but will do more to maintain physics simulation independent of frame rate
            dynamicsWorld.stepSimulation(delta, 5, 1/60f);
            for (T obj : instances) {
                btRigidBody body = renderer.getBulletBody(obj);
                ModelInstance inst = renderer.getRenderable(obj);
                if(body != null) {
                    body.getWorldTransform(inst.transform);
                }
            }
            super.render(delta);
            debugRenderer.update(renderer().camera());
        }

        @Override
        public void dispose() {
            super.dispose();
            dynamicsWorld.dispose();
            constraintSolver.dispose();
            broadphase.dispose();
            dispatcher.dispose();
            configuration.dispose();
        }
    }


}