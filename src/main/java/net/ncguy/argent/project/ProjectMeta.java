package net.ncguy.argent.project;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import net.ncguy.argent.utils.FileUtils;

import java.io.File;

/**
 * Created by Guy on 25/08/2016.
 */
public class ProjectMeta {

    public String name = "";
    public String path = "";
    public String lastAccessed = "";
    public String created = "";
    public transient long size = 0L;


    public ProjectMeta() {
    }

    public String size() {
        return FileUtils.getFileSizeString(this.size);
    }

    public ProjectMeta calculateSize() {
        File file = new File(this.path);
        if(!file.exists()) {
            System.out.println("File does not exist");
            this.size = -1L;
            return this;
        }
        this.size = FileUtils.pathSize(file.toPath());
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof ProjectMeta)) return false;
        if(this == obj) return true;
        ProjectMeta that = (ProjectMeta)obj;
        if(this.name.equals(that.name) && this.path.equals(that.path)) return true;
        return super.equals(obj);
    }

    public String path() {
        if(path == null) return null;
        if(!path.endsWith("/"))
            path += "/";
        return path;
    }

    public static class MetaSerializer extends Serializer<ProjectMeta> {

        @Override
        public void write(Kryo kryo, Output output, ProjectMeta object) {
            kryo.writeObject(output, object.name);
            kryo.writeObject(output, object.path);
            kryo.writeObject(output, object.lastAccessed);
            kryo.writeObject(output, object.created);
        }
        @Override
        public ProjectMeta read(Kryo kryo, Input input, Class<ProjectMeta> type) {
            ProjectMeta meta = new ProjectMeta();

            meta.name = kryo.readObject(input, String.class);
            meta.path = kryo.readObject(input, String.class);
            meta.lastAccessed = kryo.readObject(input, String.class);
            meta.created = kryo.readObject(input, String.class);
            meta.calculateSize();
            return meta;
        }

    }

}
