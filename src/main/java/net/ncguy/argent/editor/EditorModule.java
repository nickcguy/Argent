package net.ncguy.argent.editor;

import net.ncguy.argent.IModule;
import net.ncguy.argent.editor.tools.ToolManager;
import net.ncguy.argent.editor.tools.picker.ToolHandlePicker;
import net.ncguy.argent.editor.tools.picker.WorldEntityPicker;
import net.ncguy.argent.entity.EntityModule;
import net.ncguy.argent.event.EventModule;
import net.ncguy.argent.injector.InjectionModule;
import net.ncguy.argent.injector.InjectionStore;
import net.ncguy.argent.ui.UIModule;
import net.ncguy.argent.world.ProjectModule;

/**
 * Created by Guy on 17/07/2016.
 */
public class EditorModule extends IModule {

    @Override
    public void init() {
        CommandHistory commandHistory = new CommandHistory();
        try {
            InjectionStore.setGlobal(commandHistory);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        ToolManager toolManager = new ToolManager(new WorldEntityPicker(), new ToolHandlePicker());
        try {
            InjectionStore.setGlobal(toolManager);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        EditorUI editorUI = new EditorUI();
        try {
            InjectionStore.setGlobal(editorUI);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String moduleName() {
        return "Editor";
    }

    @Override
    public Class<IModule>[] dependencies() {
        return new Class[]{EventModule.class, InjectionModule.class, ProjectModule.class, EntityModule.class, UIModule.class};
    }
}
