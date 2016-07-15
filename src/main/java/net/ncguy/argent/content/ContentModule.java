package net.ncguy.argent.content;

import net.ncguy.argent.Argent;
import net.ncguy.argent.IModule;

/**
 * Created by Guy on 15/07/2016.
 */
public class ContentModule extends IModule {

    public ContentModule() {
        Argent.content = new ContentManager();
    }

    @Override
    public String moduleName() {
        return "Content loading";
    }

}
