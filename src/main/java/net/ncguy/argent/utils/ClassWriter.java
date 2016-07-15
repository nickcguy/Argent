package net.ncguy.argent.utils;

import java.lang.reflect.Field;

/**
 * Created by Guy on 12/07/2016.
 */
public class ClassWriter {

    public static String generateReflectionMethod(Field field) {
        return generateReflectionMethod(Visibility.PUBLIC, field);
    }

    public static String generateReflectionMethod(Visibility visibility, Field field) {
        StringBuilder sb = new StringBuilder();
        String fieldName = "$gen_"+field.getName();
        String castStr = "("+field.getType().getCanonicalName()+")";
        sb.append("private java.lang.reflect.Field ").append(fieldName).append(";\n");
        sb.append(visibility.toString()).append(" ").append(field.getType().getCanonicalName()).append(" $getValue__").append(field.getName()).append("() throws Exception {\n");
        sb.append("\tif(").append(fieldName).append(" == null) {\n");
        sb.append("\t\t").append(fieldName).append(" = ").append(field.getDeclaringClass().getCanonicalName()).append(".class.getDeclaredField(\"").append(field.getName()).append("\");\n");
        sb.append("\t\t").append(ReflectionUtils.class.getCanonicalName()).append(".setCompletelyAccessible(").append(fieldName).append(");\n");
        sb.append("\t}\n");
        sb.append("\treturn ").append(castStr).append(fieldName).append(".get(this);\n");
        sb.append("}\n");
        sb.append(visibility.toString()).append(" void $setValue_").append(field.getName()).append("(").append(field.getType().getCanonicalName()).append(" var) throws Exception {\n");
        sb.append("\tif(").append(fieldName).append(" == null) {\n");
        sb.append("\t\t").append(fieldName).append(" = ").append(field.getDeclaringClass().getCanonicalName()).append(".class.getDeclaredField(\"").append(field.getName()).append("\");\n");
        sb.append("\t\t").append(ReflectionUtils.class.getCanonicalName()).append(".setCompletelyAccessible(").append(fieldName).append(");\n");
        sb.append("\t}\n");
        sb.append("\t").append(fieldName).append(".set(this, ").append(castStr).append("var);\n");
        sb.append("}\n");
        return sb.toString();
    }

    enum Visibility {
        PACKAGE(""),
        PUBLIC("public"),
        PROTECTED("protected"),
        PRIVATE("private"),
        ;
        Visibility(String txt) {
            this.txt = txt;
        }
        public final String txt;

        @Override
        public String toString() {
            return txt;
        }
    }

}
