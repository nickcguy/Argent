package net.ncguy.argent.asset.loader;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import net.ncguy.argent.asset.ArgentAsset;

import java.io.IOException;

/**
 * Created by Guy on 09/06/2016.
 */
public class ArgentAssetLoader extends AsynchronousAssetLoader<ArgentAsset, ArgentAssetLoader.ArgentAssetParameter> {

    ArgentAsset asset;

    public ArgentAssetLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, ArgentAssetParameter parameter) {
        this.asset = null;
        try {
            this.asset = ArgentAsset.loadFromArchive(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArgentAsset loadSync(AssetManager manager, String fileName, FileHandle file, ArgentAssetParameter parameter) {
        ArgentAsset asset = this.asset;
        this.asset = null;
        return asset;
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, ArgentAssetParameter parameter) {
        return null;
    }

    public static class ArgentAssetParameter extends AssetLoaderParameters<ArgentAsset> {}


}
