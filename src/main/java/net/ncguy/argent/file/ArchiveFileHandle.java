package net.ncguy.argent.file;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import net.ncguy.argent.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by Guy on 09/06/2016.
 */
public class ArchiveFileHandle extends FileHandle {

    final ZipFile archive;
    final ZipEntry archiveEntry;

    public ArchiveFileHandle(ZipFile archive, File file) {
        super(file, Files.FileType.Classpath);
        this.archive = archive;
        archiveEntry = this.archive.getEntry(file.getPath());
    }

    public ArchiveFileHandle(ZipFile archive, String fileName) {
        super(FileUtils.formatFilePath(fileName), Files.FileType.Classpath);
        this.archive = archive;
        this.archiveEntry = archive.getEntry(FileUtils.formatFilePath(fileName));
    }

    @Override
    public FileHandle child(String name) {
        name = FileUtils.formatFilePath(name);
        if(file.getPath().length() == 0) return new ArchiveFileHandle(archive, new File(name));
        return new ArchiveFileHandle(archive, new File(file, name));
    }

    @Override
    public FileHandle sibling(String name) {
        name = FileUtils.formatFilePath(name);
        if(file.getPath().length() == 0) throw new GdxRuntimeException("Cannot get the sibling of root");
        return new ArchiveFileHandle(archive, new File(file.getParent(), name));
    }

    @Override
    public FileHandle parent() {
        File parent = file.getParentFile();
        if(parent == null) {
            if(type == Files.FileType.Absolute) parent = new File("/");
            else parent = new File("");
        }
        return new ArchiveFileHandle(archive, parent);
    }

    @Override
    public InputStream read() {
        try {
            return archive.getInputStream(archiveEntry);
        }catch (IOException ioe) {
            throw new GdxRuntimeException("File not found: "+file+" (Archive)");
        }
    }

    @Override public boolean exists() { return archiveEntry != null; }
    @Override public long length() {
        if(archiveEntry == null) return 0;
        return archiveEntry.getSize();
    }
    @Override public long lastModified() { return archiveEntry.getTime(); }
}
