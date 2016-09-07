package net.ncguy.argent.utils;

import com.badlogic.gdx.files.FileHandle;
import net.ncguy.argent.GlobalSettings;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * Created by Guy on 15/07/2016.
 */
public class FileUtils {

    public static List<FileHandle> getAllHandlesInDirectory(FileHandle root) {
        List<FileHandle> list = new ArrayList<>();
        if(root.isDirectory()) {
            FileHandle[] children = root.list();
            for (FileHandle child : children) {
                list.addAll(getAllHandlesInDirectory(child));
            }
        }else{
            list.add(root);
        }
        return list;
    }

    public static List<File> getAllFilesInDirectory(File root) {
        List<File> files = new ArrayList<>();
        getAllFilesInDirectory(root, files);
        return files;
    }
    public static void getAllFilesInDirectory(File root, List<File> files) {
        if(root.isDirectory()) {
            File[] children = root.listFiles();
            if(children != null)
                for(File child : children) getAllFilesInDirectory(child, files);
        }else{
            files.add(root);
        }
    }

    public static String getUserDirectoryPath() {
        return System.getProperty("user.home");
    }

    public static String getFileExtension(File file) {
        return getFileExtension(file.getName());
    }
    public static String getFileExtension(String fileName) {
        if(!fileName.contains(".")) return fileName;
        int index = fileName.lastIndexOf('.');
        return fileName.substring(index);
    }

    public static long folderSize(File file) {
        long length = 0;
        if(file == null) return 0L;
        File[] files = file.listFiles();
        if(files == null) return 0L;
        for (File f : files) {
            if(f.isDirectory()) length += folderSize(f);
            else length += f.length();
        }
        return length;
    }

    public static long pathSize(Path path) {
        final AtomicLong size = new AtomicLong(0);
        try{
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    size.addAndGet(attrs.size());
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    System.out.println("Skipped "+ file + " (" + exc + ")");
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    if(exc != null)
                        System.out.println("Error encountered while traversing: "+dir+" ("+exc+")");
                    return FileVisitResult.CONTINUE;
                }
            });
        }catch (IOException e) {

        }
        return size.get();
    }

    public static int getFileDescendantCount(File root) {
        final int[] count = {0};
        try {
            Files.walkFileTree(root.toPath(), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    count[0]++;
                    return super.visitFile(file, attrs);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return count[0];
    }

    public static void traverseFileTree(Path root, Consumer<TraversalStep> onFileStep) throws IOException {
        if(!Files.exists(root)) return;
        Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                onFileStep.accept(new TraversalStep(file, true));
                return super.visitFile(file, attrs);
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                onFileStep.accept(new TraversalStep(file, false));
                return super.visitFileFailed(file, exc);
            }
        });
    }

    public static String getFileSizeString(long bytes) {
        int mod = 1000;
        if(GlobalSettings.binaryFileSizes) mod = 1024;

        String suffix = "B";

        if(bytes > mod) {
            bytes /= mod;
            suffix = "KB";
        }
        if(bytes > mod) {
            bytes /= mod;
            suffix = "MB";
        }
        if(bytes > mod) {
            bytes /= mod;
            suffix = "GB";
        }
        return bytes + " " + suffix;
    }

    public static String getFileSizeString(File file) {
        return getFileSizeString(file.toPath());
    }

    public static String getFileSizeString(Path path) {
        return getFileSizeString(pathSize(path));
    }

    public static class TraversalStep {
        public Path path;
        public boolean success;

        public TraversalStep(Path path, boolean success) {
            this.path = path;
            this.success = success;
        }
    }

}
