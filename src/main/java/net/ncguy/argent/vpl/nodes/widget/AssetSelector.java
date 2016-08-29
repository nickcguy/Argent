package net.ncguy.argent.vpl.nodes.widget;

import net.ncguy.argent.assets.ArgAsset;
import net.ncguy.argent.editor.project.ProjectManager;
import net.ncguy.argent.injector.ArgentInjector;
import net.ncguy.argent.injector.Inject;
import net.ncguy.argent.ui.SearchableList;

import java.util.List;

/**
 * Created by Guy on 28/08/2016.
 */
public abstract class AssetSelector<T extends ArgAsset> extends SearchableList<T> {

    @Inject
    ProjectManager manager;

    public AssetSelector() {
        super();
        ArgentInjector.inject(this);
        populateList();
    }

    public abstract List<T> getAssets();

    protected void populateList() {
        List<T> assets = getAssets();
        assets.forEach(asset -> {
            Item<T> item = new Item<>(asset.icon(), asset.name(), asset);
            addItem(item);
        });
    }


}
