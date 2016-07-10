package net.ncguy.argent.utils.search;

public class StringDifference {

    public static int getDifference(String a, String b, IStringDifference difference) {
        return difference.distance(a, b);
    }

    public static int getDifference_DamerauLevenshtein(String a, String b) {
        return getDifference(a, b, new DamerauLevenshtein.Alignment());
    }

}