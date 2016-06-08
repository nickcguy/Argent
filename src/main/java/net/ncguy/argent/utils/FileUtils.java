package net.ncguy.argent.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guy on 08/06/2016.
 */
public class FileUtils {

    public static List<File> getAllFilesInDirectory(File dir) {
        return getAllFilesInDirectory(dir, true);
    }
    public static List<File> getAllFilesInDirectory(File dir, boolean recursive) {
        List<File> files = new ArrayList<>();
        if(dir == null) return files;
        File[] fileArr = dir.listFiles();
        if(fileArr == null) return files;
        for(File f : fileArr) {
            files.add(f);
            if(recursive) {
                if(f.isDirectory())
                    files.addAll(getAllFilesInDirectory(f));
            }

        }
        return files;
    }

    public static String getFileExtension(File file) {
        return file.getName().substring(file.getName().lastIndexOf('.')+1);
    }


}
