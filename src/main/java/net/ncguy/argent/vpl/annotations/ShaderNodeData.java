package net.ncguy.argent.vpl.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Guy on 29/08/2016.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
public @interface ShaderNodeData {

    String uniforms() default "";
    String fragment() default "";
    String variable() default "";

    public static class Packet {
        public String uniforms;
        public String fragment;
        public String variable;

        public Packet(ShaderNodeData data) {
            this(data.uniforms(), data.fragment(), data.variable());
        }

        public Packet(String uniforms, String fragment, String variable) {
            this.uniforms = uniforms;
            this.fragment = fragment;
            this.variable = variable;
        }
    }

}
