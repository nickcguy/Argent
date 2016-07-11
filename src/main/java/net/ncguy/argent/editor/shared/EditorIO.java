package net.ncguy.argent.editor.shared;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;
import net.ncguy.argent.Argent;
import net.ncguy.argent.io.IWritable;
import net.ncguy.argent.render.shader.DynamicShader;
import net.ncguy.argent.world.GameWorld;
import net.ncguy.argent.world.WorldObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guy on 09/07/2016.
 */
public class EditorIO<T> {

    private GameWorld.Generic<T> world;
    private Stage stage;
    private FileChooser chooser;

    public EditorIO(Stage stage, GameWorld.Generic<T> world) {
        this.stage = stage;
        this.world = world;
        this.chooser = new FileChooser(FileChooser.Mode.SAVE);
        this.chooser.setMultiSelectionEnabled(false);
    }

    public void save() {
        chooser.setMode(FileChooser.Mode.SAVE);
        chooser.setDirectory(new File(""));
        chooser.setListener(new FileChooserAdapter(){
            @Override
            public void selected(Array<FileHandle> files) {
                FileHandle first = files.first();
                if(first != null)
                    save(first.file());
            }
        });
        this.chooser.centerWindow();
        this.stage.addActor(this.chooser.fadeIn());
    }

    public void load() {
        chooser.setMode(FileChooser.Mode.OPEN);
        chooser.setDirectory(new File(""));
        chooser.setListener(new FileChooserAdapter(){
            @Override
            public void selected(Array<FileHandle> files) {
                FileHandle first = files.first();
                if(first != null) try {
                    load(first.file());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        this.chooser.centerWindow();
        this.stage.addActor(this.chooser.fadeIn());
    }

    public void save(File file) {
        if(file.isDirectory()) {
            System.out.println("Not a file");
            return;
        }
        Path path = file.toPath();
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        GameWorldSaveState saveState = new GameWorldSaveState<>(world);
        saveState.packData();
        Argent.serial.serialize(saveState, (bin) -> {
            try {
                Files.write(path, bin.toString().getBytes(), StandardOpenOption.CREATE_NEW , StandardOpenOption.WRITE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void load(File file) throws IOException {
        if(file.isDirectory()) {
            System.out.println("Not a file");
            return;
        }
        Path path = file.toPath();
        List<String> lines = Files.readAllLines(path);
        StringBuilder sb = new StringBuilder();
        lines.forEach(s -> sb.append(s).append("\n"));
        String s = sb.toString();
        world.instances().clear();
        WorldObjectSaveState state = Argent.serial.deserialize(s, WorldObjectSaveState.class);
        state.unpackData();
        state.instances.forEach(((GameWorld.Generic<WorldObject>)world)::addInstance);
//        world.instances().addAll);
        world.renderer().clearRenderPipe();
        world.renderer().compileDynamicRenderPipe(state.renderers);
    }

    public static class WorldObjectSaveState extends GameWorldSaveState<WorldObject> {}

    public static class GameWorldSaveState<T> implements IWritable {
        public List<T> instances;
        public List<DynamicShader.Info> renderers;
        public transient GameWorld.Generic<T> world;

        public GameWorldSaveState() {
            this.world = null;
            this.instances = new ArrayList<>();
            this.renderers = new ArrayList<>();
        }

        public GameWorldSaveState(GameWorld.Generic<T> world) {
            this.world = world;
            this.instances = this.world.instances();
            this.renderers = new ArrayList<>();
            this.world.renderer().dynamicPipe().forEach(p -> this.renderers.add(p.info()));
        }

        @Override
        public void packData() {
            this.instances.forEach(i -> {
                if(i instanceof IWritable) ((IWritable) i).packData();
            });
            this.renderers.forEach(i -> {
                if(i instanceof IWritable) ((IWritable) i).packData();
            });
        }

        @Override
        public void unpackData() {
            this.instances.forEach(i -> {
                if(i instanceof IWritable) ((IWritable) i).unpackData();
            });
            this.renderers.forEach(i -> {
                if(i instanceof IWritable) ((IWritable) i).unpackData();
            });
        }
    }

}
