package net.ncguy.argent.event;

import net.ncguy.argent.assets.ArgMaterial;

/**
 * Created by Guy on 01/08/2016.
 */
public class MaterialModifiedEvent extends AbstractEvent {

    ArgMaterial mtl;

    public MaterialModifiedEvent() {}

    public MaterialModifiedEvent(ArgMaterial mtl) {
        this.mtl = mtl;
    }

    public ArgMaterial getMtl() { return mtl; }
    public void setMtl(ArgMaterial mtl) {this.mtl = mtl; }

    public static interface MaterialModifiedListener {
        @Subscribe
        public void onMaterialModified(MaterialModifiedEvent event);
    }

}
