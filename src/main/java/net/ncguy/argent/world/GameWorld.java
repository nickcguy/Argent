package net.ncguy.argent.world;

import com.badlogic.gdx.Input;
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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guy on 13/06/2016.
 */
public class GameWorld {
    public static class Generic<T> implements Disposable {

        protected transient WorldRenderer<T> renderer;
        protected List<T> instances;
        protected transient Console console;
        protected transient T selected;

        public transient Skin consoleSkin;
        public transient boolean consoleEnabled = false;
        public transient boolean showBuffers = false;

        protected transient List<VarRunnables.VarRunnable<T>> onSelect = new ArrayList<>();

        public Generic(WorldRenderer<T> renderer, List<T> instances) {
            this.instances = instances;
            this.renderer = renderer;
        }

        public T selected() {
            if(selected == null)
                select(instances.get(0));
            return selected;
        }
        public Generic select(T selected) {
            this.selected = selected;
            this.onSelect.forEach(s -> s.run(selected));
            return this;
        }

        public void addOnSelect(VarRunnables.VarRunnable<T> onSelect) { this.onSelect.add(onSelect); }
        public void removeOnSelect(VarRunnables.VarRunnable<T> onSelect) { this.onSelect.remove(onSelect); }

        public void addInstance(T instance) {
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
    }

    public static class Physics<T> extends Generic<T> {

        public enum WorldFlags {
            FLAG0(0),           // 0
            FLAG1(1),           // 1
            FLAG2(1<<1),        // 2
            FLAG3(1<<2),        // 4
            FLAG4(1<<3),        // 8
            FLAG5(1<<4),        // 16
            FLAG6(1<<5),        // 32
            FLAG7(1<<6),        // 64
            FLAG8(1<<7),        // 128
            FLAG9(1<<8),        // 256
            FLAG10(1<<9),       // 512
            FLAG11(1<<10),      // 1,024
            FLAG12(1<<11),      // 2,048
            FLAG13(1<<12),      // 4,096
            FLAG14(1<<13),      // 8,192
            FLAG15(1<<14),      // 16,384
            FLAG16(1<<15),      // 32,768
            FLAG17(1<<16),      // 65,536
            FLAG18(1<<17),      // 131,072
            FLAG19(1<<18),      // 262,144
            FLAG20(1<<19),      // 524,288
            FLAG21(1<<20),      // 1,048,576
            FLAG22(1<<21),      // 2,097,152
            FLAG23(1<<22),      // 4,194,304
            FLAG24(1<<23),      // 8,388,608
            FLAG25(1<<24),      // 16,777,216
            FLAG26(1<<25),      // 33,554,432
            FLAG27(1<<26),      // 67,108,864
            FLAG28(1<<27),      // 134,217,728
            FLAG29(1<<28),      // 268,435,456
            FLAG30(1<<29),      // 536,870,912
            FLAG31(1<<30),      // 1,073,741,824
            ALL(Integer.MAX_VALUE),
            ;
            WorldFlags(int i) {
                this.flag = i;
            }
            public int flag;
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
            super.addInstance(instance);
            btRigidBody body = renderer.getBulletBody(instance);
            if(body != null)
                dynamicsWorld.addRigidBody(body);
        }


        @Override
        public void removeInstance(T instance) {
            super.removeInstance(instance);
            btRigidBody body = renderer.getBulletBody(instance);
            if(body != null)
                dynamicsWorld.removeRigidBody(body);
        }

        @Override
        public void render(float delta) {
            // Restricts physics update to a maximum of 5 per frame.
            // Should only do 1 though, but will do more to maintain physics simulation independent of frame rate
            dynamicsWorld.stepSimulation(delta, 5, 1/60f);
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