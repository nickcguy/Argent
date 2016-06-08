package net.ncguy.argent.asset.loader;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * Created by Guy on 08/06/2016.
 */
public class StringLoader extends AsynchronousAssetLoader<String, StringLoader.StringParameter> {

    public StringLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    String string;

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, StringParameter parameter) {
        this.string = null;
        StringBuilder sb = new StringBuilder();
        try {
            List<String> lines = Files.readAllLines(file.file().toPath());
            lines.forEach(l -> sb.append(l).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.string = sb.toString();
    }

    @Override
    public String loadSync(AssetManager manager, String fileName, FileHandle file, StringParameter parameter) {
        String str = this.string;
        this.string = null;
        return str;
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, StringParameter parameter) {
        return null;
    }

    public static class StringParameter extends AssetLoaderParameters<String> {}

}
