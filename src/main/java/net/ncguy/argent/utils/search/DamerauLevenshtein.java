package net.ncguy.argent.utils.search;

import net.ncguy.argent.utils.MathsUtils;

/**
 * Created by Guy on 07/07/2016.
 */
public class DamerauLevenshtein {

    public static class Alignment implements IStringDifference {
        @Override
        public int distance(String strA, String strB) {
            char[] a = strA.toCharArray();
            char[] b = strB.toCharArray();
            int[][] d = new int[strA.length()+1][strB.length()+1];


            for(int i = 0; i <= strA.length(); i++) d[i][0] = i;
            for(int j = 0; j <= strB.length(); j++) d[0][j] = j;

            int cost;

            for(int i = 1; i < strA.length(); i++) {
                for(int j = 1; j < strB.length(); j++) {
                    if(a[i] == b[j]) cost = 0;
                    else cost = 1;
                    d[i][j] = MathsUtils.tMin(
                            d[i-1][j]+1,        // Deletion
                            d[i][j-1]+1,        // Insertion
                            d[i-1][j-1]+cost    // Substitution
                    );
                    if(i > 1 && j > 1 && a[i] == b[j-1] && a[i-1] == b[j])
                        d[i][j] = Math.min(d[i][j], d[i-2][j-2]+cost); // Transposition
                }
            }
            System.out.println(d[strA.length()][strB.length()]);
            return d[strA.length()][strB.length()];
        }
    }

    public static class Transposition implements IStringDifference {
        @Override
        public int distance(String a, String b) {

            return 0;
        }
    }

}
