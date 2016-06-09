package net.ncguy.argent.asset;

import com.badlogic.gdx.files.FileHandle;
import net.ncguy.argent.file.ArchiveFileHandle;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by Guy on 09/06/2016.
 */
public class ArgentAsset {

    private AssetMetadata meta;
    private Map<String, ?> assets;

    public ArgentAsset(AssetMetadata meta) {
        this.meta = meta;
        this.assets = new HashMap<>();
    }

    public static ArgentAsset loadFromArchive(FileHandle archiveHandle) throws IOException {
        ZipFile archive = new ZipFile(archiveHandle.file());
        ArchiveFileHandle handle = new ArchiveFileHandle(archive, new File(""));
        FileHandle metaFile = handle.child("asset.meta");
        AssetMetadata meta = AssetMetadata.readFromFile(metaFile);
        ArgentAsset asset = new ArgentAsset(meta);

        meta.assetMap().forEach((k, v) -> {
            ZipEntry e = archive.getEntry(v);
            if(e == null) return;
        });

        return asset;
    }

    public <T> T get(String key) {
        Object obj = meta.assetMap().get(key);
        if(obj == null) return null;
        return (T) obj;
    }

}
