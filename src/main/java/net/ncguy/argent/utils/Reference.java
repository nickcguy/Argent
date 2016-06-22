package net.ncguy.argent.utils;

import com.badlogic.gdx.math.Matrix4;

/**
 * Created by Guy on 22/06/2016.
 */
public class Reference {

    public static class Matrix4Alias {

        public static final int TranslationX = Matrix4.M03;
        public static final int TranslationY = Matrix4.M13;
        public static final int TranslationZ = Matrix4.M23;

        public static final int ScaleX = Matrix4.M00;
        public static final int ScaleY = Matrix4.M11;
        public static final int ScaleZ = Matrix4.M22;

    }

}
