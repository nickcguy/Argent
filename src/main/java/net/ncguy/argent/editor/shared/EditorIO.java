package net.ncguy.argent.editor.shared;

import net.ncguy.argent.Argent;
import net.ncguy.argent.render.shader.DynamicShader;
import net.ncguy.argent.world.GameWorld;

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

    public EditorIO(GameWorld.Generic<T> world) {
        this.world = world;
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
        Argent.serial.serialize(new GameWorldSaveState<>(world), (bin) -> {
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
        GameWorldSaveState state = Argent.serial.deserialize(s, GameWorldSaveState.class);

        world.instances().addAll(state.instances);
        world.renderer().clearRenderPipe();
        world.renderer().compileDynamicRenderPipe(state.renderers);
    }

    public static class GameWorldSaveState<T> {
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
    }

}
