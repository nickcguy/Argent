package net.ncguy.argent.vpl;

import net.ncguy.argent.vpl.inspector.NodeRegistrationInspector;
import net.ncguy.argent.vpl.node.VPLBaseNode;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Guy on 09/06/2016.
 */
public class VPLCore {

    private Reflections reflections;
    private NodeRegistrationInspector inspector;

    public VPLCore(String pkg) {
        setPackage(pkg);
        setInspector(new NodeRegistrationInspector());
    }

    public void setPackage(String pkg) {
        this.reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(pkg))
                .setScanners(new MethodAnnotationsScanner())
                .filterInputsBy(new FilterBuilder().includePackage(pkg)));
    }

    public void setInspector(NodeRegistrationInspector inspector) { this.inspector = inspector; }
    public Set<Method> collectMethods(Class<? extends Annotation> markerAnnotation) { return reflections.getMethodsAnnotatedWith(markerAnnotation); }

    public List<VPLBaseNode> buildNodes(Set<Method> methods) {
        final List<VPLBaseNode> nodes = new ArrayList<>();
        methods.forEach(m -> {
            VPLBaseNode node = new VPLBaseNode(m);
            String result = inspector.inspect(node);
            if(result == null || result.length() == 0) nodes.add(node);
        });
        return nodes;
    }

}
