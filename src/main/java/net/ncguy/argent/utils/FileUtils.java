package net.ncguy.argent.utils;

import com.badlogic.gdx.files.FileHandle;

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

}
