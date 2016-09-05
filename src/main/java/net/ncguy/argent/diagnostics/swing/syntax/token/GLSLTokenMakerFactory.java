package net.ncguy.argent.diagnostics.swing.syntax.token;

import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;

/**
 * Created by Guy on 05/09/2016.
 */
public class GLSLTokenMakerFactory extends AbstractTokenMakerFactory {

    public static final String GLSL = "text/glsl";

    @Override
    protected void initTokenMakerMap() {
        putMapping(GLSL, GLSLTokenMaker.class.getName());
    }
}
