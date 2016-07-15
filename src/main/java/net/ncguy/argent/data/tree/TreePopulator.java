package net.ncguy.argent.data.tree;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Created by Guy on 15/07/2016.
 */
public class TreePopulator {

    public static <T, U> void populate(VisitableTree<T> tree, List<U> data, String delim, Function<U, String> stringLoc, BiFunction<U, String, T> nodeLoc) {
        VisitableTree<T> current = tree;
        for (U d : data) {
            VisitableTree<T> root = current;
            String pathStr = stringLoc.apply(d);
            String[] path = pathStr.split(delim);
            String p = "";
            for (String cat : path) {
                if(p.length() == 0) p = cat;
                else p += "|"+cat;
                current = current.child(nodeLoc.apply(d, p));
            }

            current = root;
        }
    }

}
