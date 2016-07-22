package net.ncguy.argent.utils;

import com.badlogic.gdx.files.FileHandle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

}
