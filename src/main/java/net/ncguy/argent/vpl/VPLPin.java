package net.ncguy.argent.vpl;

/**
 * Created by Guy on 18/08/2016.
 */
public class VPLPin {

    enum Types {
        INPUT,
        OUTPUT,
        ARRAY
        ;
        public int getBit() {
            return (int) Math.pow(2, ordinal());
        }

        public static int Mask = INPUT.getBit() | OUTPUT.getBit() | ARRAY.getBit();
    }

    public boolean is (final long mask) {
        return (mask & Types.Mask) != 0;
    }
    public boolean is (Types type) { return is(type.getBit()); }

    protected Class<?> cls;

}
