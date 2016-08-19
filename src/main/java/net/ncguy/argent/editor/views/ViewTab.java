package net.ncguy.argent.editor.views;

import com.kotcrab.vis.ui.widget.tabbedpane.Tab;

/**
 * Created by Guy on 16/08/2016.
 */
public abstract class ViewTab extends Tab {

    public ViewTab() {}

    public ViewTab(boolean savable) { super(savable); }

    public ViewTab(boolean savable, boolean closeableByUser) { super(savable, closeableByUser); }

    public void onOpen() {}
    public void onClose() {}

}
