package net.ncguy.argent.parser;

import net.ncguy.argent.utils.TextUtils;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by Guy on 24/06/2016.
 */
public class GLError {

    public int line;
    public Type type;
    public String text;

    @Override
    public String toString() {
        String out = type.name()+"\n" +
                "Line: "+line+"\n" +
                text;
        return out;
    }

    public static class Parser implements IParser<GLError> {

        public Set<GLError> parse(String log) {
            Set<GLError> errorList = new LinkedHashSet<>();
            String[] logLines = log.split("\n");
            for(String line : logLines) {
                if(line.length() <= 3) continue;
                GLError error = new GLError();
                error.line = getErrorLine(line);
                error.text = getErrorText(line);
                error.type = getErrorType(line);
                errorList.add(error);
            }
            return errorList;
        }

        private static int getErrorLine(String line) {
            String text = line.split(":")[0];
            String lineNumber = text.split("[\\(\\)]")[1];
            if(!TextUtils.isInteger(lineNumber)) return 0;
            return Integer.parseInt(lineNumber);
        }

        private static GLError.Type getErrorType(String line) {
            String text = line.split(":")[1];
            text = text.trim();
            text = text.split(" ")[0];
            return GLError.Type.valueOf(text.toUpperCase());
        }

        private static String getErrorText(String line) {
            String[] text = line.split(":");
            StringBuilder sb = new StringBuilder();
            for (int i = 2; i < text.length; i++)
                sb.append(text[i]).append(":");
            String str = sb.toString();
            return str.substring(0, str.length()-1);
        }

    }

    enum Type {
        WARNING,
        ERROR,
        ;
    }

}
