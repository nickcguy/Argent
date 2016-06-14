package net.ncguy.argent.asset;

import com.badlogic.gdx.assets.AssetManager;
import net.ncguy.argent.asset.loader.ArgentAssetLoader;
import net.ncguy.argent.utils.FileUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Guy on 08/06/2016.
 */
public class ContentManager {

    private Map<String, String> assetMap;
    private Map<String, Class<?>> assetTypes;

    private AssetManager manager;
    private LoaderCallback finishedCallback;
    private FileRoot[] fileRoots;
    private boolean hasLoaded = false;
    private boolean hasFinished = false;
    private boolean canUpdate = false;


    public ContentManager() {
        initLoader();
    }

    private void initLoader() {
        manager = new AssetManager();
        manager.setLoader(ArgentAsset.class, new ArgentAssetLoader(manager.getFileHandleResolver()));
        assetMap = new HashMap<>();
        assetTypes = new HashMap<>();
        fileRoots = new FileRoot[0];
    }

    public void addDirectoryRoot(File root, Class<?> assetType, String... extensionFilter) {
        addDirectoryRoot(new FileRoot(root, assetType, extensionFilter));
    }

    public void addDirectoryRoot(FileRoot root) {
        FileRoot[] newArr = new FileRoot[this.fileRoots.length+1];
        int i = 0;
        for(i = 0; i < this.fileRoots.length; i++) {
            newArr[i] = this.fileRoots[i];
        }
        newArr[i] = root;
        this.fileRoots = newArr;
    }

    public void setDirectoryRoots(FileRoot... roots) {
        this.fileRoots = roots;
    }

    public void setOnFinish(LoaderCallback runnable) {
        finishedCallback = runnable;
    }

    public void start() {
        new Thread(() -> {
            for(FileRoot root : fileRoots) {
                List<File> files = FileUtils.getAllFilesInDirectory(root.root);
                files.forEach(f -> {
                    if(root.isExtensionValid(f)) {
                        String key = root.assetType.getSimpleName()+"_"+FileUtils.getFileName(f);
                        assetMap.put(key, FileUtils.formatFilePath(f.getPath()));
                        assetTypes.put(key, root.assetType);
                        manager.load(f.getPath(), root.assetType);
                    }
                });
            }
            canUpdate = true;
        }).start();
    }

    public <T> T get(String ref, Class<T> cls) {
        return (T)get(ref);
    }

    public <T> T get(String ref) {
        if(!assetMap.containsKey(ref))
            return null;
        String val = assetMap.get(ref);
        return manager.get(val);
    }

    public float progress() {
        if(!canUpdate) return 0;
        if(hasFinished) return 1;
        return manager.getProgress();
    }

    public boolean update() {
        if(!canUpdate) return false;
        if(hasFinished) return true;
        boolean done = manager.update();
        if (done) {
            hasFinished = true;
            if(finishedCallback != null)
                finishedCallback.done(manager);
        }
        return done;
    }

    public Map<String, String> assetMap() { return assetMap; }

    public interface LoaderCallback {
        void done(AssetManager manager);
    }

    public static class FileRoot {
        public File root;
        public Class<?> assetType;
        public String[] extensionFilters;

        public FileRoot(File root, Class<?> assetType, String... extensionFilters) {
            this.root = root;
            this.assetType = assetType;
            this.extensionFilters = extensionFilters;
        }

        public boolean isExtensionValid(String ext) {
            for (String filter : this.extensionFilters) {
                if(filter.equalsIgnoreCase(ext)) return true;
            }
            return false;
        }
        public boolean isExtensionValid(File file) {
            return isExtensionValid(FileUtils.getFileExtension(file));
        }

    }


}
