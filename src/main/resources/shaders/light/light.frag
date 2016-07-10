#version 120

varying float v_depth;

void main() {
    gl_FragColor = vec4(v_depth);
}