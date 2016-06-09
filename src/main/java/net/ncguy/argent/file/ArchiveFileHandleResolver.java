package net.ncguy.argent.file;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;

import java.util.zip.ZipFile;

/**
 * Created by Guy on 09/06/2016.
 */
public class ArchiveFileHandleResolver implements FileHandleResolver {
    private final ZipFile archive;
    public ArchiveFileHandleResolver(ZipFile archive) { this.archive = archive; }
    @Override public FileHandle resolve(String fileName) { return new ArchiveFileHandle(archive, fileName); }
}
