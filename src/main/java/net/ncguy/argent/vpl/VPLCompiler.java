package net.ncguy.argent.vpl;

/**
 * Created by Guy on 09/06/2016.
 */
public class VPLCompiler {

    public static VPL compile(final VPLFactory f) {
        return new VPL() {
            final VPLFactory factory = f;
            @Override
            public void run() {

            }
        };
    }

}
