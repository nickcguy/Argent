package net.ncguy.argent.entity;

import net.ncguy.argent.IModule;
import net.ncguy.argent.entity.components.ArgentComponent;
import net.ncguy.argent.entity.components.ComponentData;
import net.ncguy.argent.injector.InjectionStore;
import net.ncguy.argent.world.ProjectModule;
import org.reflections.Reflections;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Guy on 15/07/2016.
 */
public class EntityModule extends IModule {

    @Override
    public void init() {
        Reflections refs = new Reflections("net.ncguy.argent");
        Set<Class<? extends ArgentComponent>> clsSet = refs.getSubTypesOf(ArgentComponent.class);
        clsSet = clsSet.stream().filter(cls -> cls.isAnnotationPresent(ComponentData.class)).collect(Collectors.toSet());
        try {
            InjectionStore.setGlobal(new ComponentSet(clsSet));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String moduleName() {
        return "Entity";
    }

    @Override
    public Class<IModule>[] dependencies() {
        return new Class[]{ProjectModule.class};
    }

}
