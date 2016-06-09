package net.ncguy.argent.asset;

import com.badlogic.gdx.files.FileHandle;
import net.ncguy.argent.Argent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Guy on 09/06/2016.
 */
public class AssetMetadata {

    private String displayName;
    private Map<String, String> assetMap = new HashMap<>();

    public static AssetMetadata readFromFile(FileHandle handle) throws IOException {
        return Argent.serial.deserialize(handle.readString(), AssetMetadata.class);
    }

    public String displayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public Map<String, String> assetMap() { return assetMap; }

}
