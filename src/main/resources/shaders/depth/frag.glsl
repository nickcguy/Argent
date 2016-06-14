#version 120

varying vec4 v_color;
varying vec4 v_position;
varying vec2 v_texCoord0;
varying float v_depth;

uniform float u_cameraFar;
uniform vec3 u_lightPos;

void main() {
    gl_FragColor = vec4(vec3(v_depth), 1.0);
}
