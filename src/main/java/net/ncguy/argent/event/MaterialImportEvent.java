package net.ncguy.argent.event;

import net.ncguy.argent.assets.ArgMaterial;

/**
 * Created by Guy on 01/08/2016.
 */
public class MaterialImportEvent extends AbstractEvent {

    private ArgMaterial mtl;

    public MaterialImportEvent() {}

    public MaterialImportEvent(ArgMaterial mtl) { this.mtl = mtl; }

    public ArgMaterial getMtl() { return mtl; }

    public void setMtl(ArgMaterial mtl) { this.mtl = mtl; }

    public static interface MaterialImportListener {
        @Subscribe
        public void onMaterialImport(MaterialImportEvent event);
    }

}
