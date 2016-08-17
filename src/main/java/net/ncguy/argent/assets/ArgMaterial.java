package net.ncguy.argent.assets;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.PopupMenu;
import net.ncguy.argent.assets.kryo.KryoManager;
import net.ncguy.argent.editor.project.Registry;
import net.ncguy.argent.injector.ArgentInjector;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by Guy on 01/08/2016.
 */
public class ArgMaterial extends ArgAsset<Material> {

    public transient boolean localAsset = true;
    protected String fileName;

    public String getFileName() {
        return fileName;
    }

    public ArgMaterial() {
        super();
    }

    public ArgMaterial(Material mtl) {
        this(mtl, false);
    }
    public ArgMaterial(Material mtl, boolean save) {
        super();
        this.asset = mtl;
    }

    public String path() {
        return projectManager.global().getFilePath() + "materials/" + this.fileName + Registry.MATERIAL_EXT;
    }

    public static ArgMaterial load(File file) {
        if(!file.exists()) return null;
        try {
            ArgMaterial mtl = KryoManager.kryoManager().load(file, ArgMaterial.class);
            if(mtl != null) {
                ArgentInjector.inject(mtl);
                return mtl;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void save() {
        save(new File(getProjectManager().global().getFilePath() + "materials/" + (this.fileName = asset.id) + Registry.MATERIAL_EXT));
    }

    public void save(File file) {
        file.getParentFile().mkdirs();
        try {
            KryoManager.kryoManager().save(file, this);
            System.out.println("Saved to "+file.getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public PopupMenu contextMenu() {
//        MenuItem export = new MenuItem("Make Global");
//        export.addListener(new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                getProjectManager().current().removeMaterial(ArgMaterial.this);
////                projectManager.global().addMaterial(ArgMaterial.this, true);
//                localAsset = false;
//                getProjectManager().current().refresh();
//            }
//        });
        MenuItem delete = new MenuItem("Delete");
        delete.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
//                if(localAsset)
                    getProjectManager().global().removeMaterial(ArgMaterial.this, true);
//                else projectManager.global().removeMaterial(ArgMaterial.this);
                getProjectManager().global().refresh();

            }
        });
        PopupMenu menu = new PopupMenu() {
            @Override
            public void showMenu(Stage stage, float x, float y) {
                super.showMenu(stage, x, y);
//                export.setDisabled(!localAsset);
            }
        };

//        menu.addItem(export);
        menu.addItem(delete);
        return menu;
    }

    @Override
    public String tag() {
        return "mtl";
    }

    @Override
    public String name() {
        return asset.id;
    }

    @Override
    public void name(String name) {
        this.asset.id = name;
    }
}
