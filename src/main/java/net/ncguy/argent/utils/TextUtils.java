package net.ncguy.argent.utils;

/**
 * Created by Guy on 10/06/2016.
 */
public class TextUtils {

    public static String padBinary(int dec, int len) {
        return String.format("%"+len+"s", Integer.toBinaryString(dec)).replace(' ', '0');
    }

//    static String getPaddedBinary(int num, int len) { return String.format("%"+len+"s", Integer.toBinaryString(num)).replace(' ', '0'); }

}
