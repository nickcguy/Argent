package net.ncguy.argent.utils;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.StringBuilder;

import java.util.Locale;

/**
 * Created by Guy on 22/07/2016.
 */
public class StringUtils {

    public static String splitCamelCase(String s) {
        return s.replaceAll(
                String.format("%s|%s|%s",
                        "(?<=[A-Z])(?=[A-Z][a-z])",
                        "(?<=[^A-Z])(?=[A-Z])",
                        "(?<=[A-Za-z])(?=[^A-Za-z])"
                ),
                " "
        );
    }

    public static String formatFloat(float f, int digits) {
        return String.format(Locale.US, "%."+ digits + "f", f);
    }

    public static String formatVector3(Vector3 vec, int digits, boolean brace) {
        StringBuilder sb = new StringBuilder();
        if(brace) sb.append("[");
        sb.append(formatFloat(vec.x, digits)).append(", ");
        sb.append(formatFloat(vec.y, digits)).append(", ");
        sb.append(formatFloat(vec.z, digits));
        if(brace) sb.append("]");
        return sb.toString();
    }

    public static String present(String s) {
        s = s.replace("_", " ");
        StringBuilder sb = new StringBuilder();
        char[] chars = s.toCharArray();
        boolean nextCap = true;
        for(char c : chars) {
            if(nextCap) {
                sb.append((c+"").toUpperCase());
                nextCap = false;
                continue;
            }
            sb.append(c);
            if(c == ' ')
                nextCap = true;
        }
        return sb.toString();
    }
}
