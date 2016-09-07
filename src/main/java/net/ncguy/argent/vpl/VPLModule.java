package net.ncguy.argent.vpl;

import net.ncguy.argent.Argent;
import net.ncguy.argent.IModule;
import net.ncguy.argent.ui.UIModule;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Guy on 18/08/2016.
 */
public class VPLModule extends IModule {

    private static final List<String> nodeFactoryLocations = new ArrayList<>();

    @Override
    public void init() {
        setDefaultNodeFactoryRoots();
        Argent.vpl = VPLManager.instance();
    }

    private void setDefaultNodeFactoryRoots() {
        nodeFactoryLocations.add(VPLNode.class.getPackage().toString().replace("package ", ""));
    }

    @Override
    public Class<IModule>[] dependencies() {
        return new Class[]{UIModule.class};
    }

    @Override
    public String moduleName() {
        return "Visual Programming Language";
    }

    public static void addNodeFactoryRoot(String pkg) {
        nodeFactoryLocations.add(pkg);
    }

    public static List<String> getNodeFactoryLocations() { return nodeFactoryLocations.stream().distinct().collect(Collectors.toList()); }
}
