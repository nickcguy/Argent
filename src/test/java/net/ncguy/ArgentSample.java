package net.ncguy;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import net.ncguy.argent.Argent;
import net.ncguy.argent.ArgentGame;
import net.ncguy.argent.content.ContentModule;
import net.ncguy.argent.data.tree.TreePopulator;
import net.ncguy.argent.data.tree.VisitableTree;
import net.ncguy.argent.data.tree.sample.PrintIndentedVisitor;
import net.ncguy.screen.LoaderScreen;

import java.util.ArrayList;

/**
 * Created by Guy on 15/07/2016.
 */
public class ArgentSample extends ArgentGame {

    @Override
    public void create() {

        ArrayList<TreeTest> testList = new ArrayList<>();
        testList.add(new TreeTest("a1|a2|a3", "test 1"));
        testList.add(new TreeTest("a1|a2|a3", "test 2"));
        testList.add(new TreeTest("a1|a2|a4", "test 1"));
        testList.add(new TreeTest("b1|b2|b3", "test 1"));
        testList.add(new TreeTest("b1|b4|b5", "test 1"));
        testList.add(new TreeTest("b1|b4|b5", "test 2"));

        VisitableTree<TreeTestWrapper> forest = new VisitableTree<>(new TreeTestWrapper("Root"));

        TreePopulator.populate(forest, testList, "\\|",
            (d) -> d.cat+"|"+d.name,
            (d, s) -> {
                if(s.equals(d.cat+"|"+d.name)) return new TreeTestWrapper(d, s);
                return new TreeTestWrapper(s);
            }
        );

        forest.accept(new PrintIndentedVisitor<>(0));

        Argent.loadModule(new ContentModule());
        setScreen(new LoaderScreen(this));
    }

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration cfg = new Lwjgl3ApplicationConfiguration();
        cfg.useOpenGL3(true, 3, 3);
        cfg.setWindowedMode(800, 600);
        new Lwjgl3Application(new ArgentSample(), cfg);
    }

    public static class TreeTest {
        public String cat;
        public String name;

        public TreeTest(String cat, String name) {
            this.cat = cat;
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public static class TreeTestWrapper {
        public TreeTest test;
        public String label;

        public TreeTestWrapper(TreeTest test, String label) {
            this.test = test;
            this.label = label;
        }

        public TreeTestWrapper(String label) { this.label = label; }

        @Override
        public String toString() {
            if(test == null)
                return label;
            return test.toString();

        }

        @Override
        public boolean equals(Object obj) {
            if(this == obj) return true;
            if(this.test != null) if(this.test.equals(obj)) return true;
            if(this.label.equals(obj)) return true;
            if(obj instanceof TreeTestWrapper) {
                TreeTestWrapper o = (TreeTestWrapper)obj;
                if(o.test != null) if(this.test.equals(o.test)) return true;
                if(this.label.equals(o.label)) return true;
            }
            return super.equals(obj);
        }
    }

}
