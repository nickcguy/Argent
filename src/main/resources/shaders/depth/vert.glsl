#version 120

attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;

uniform mat4 u_projViewTrans;
uniform float u_cameraFar;

varying vec4 v_color;
varying vec2 v_texCoord0;
varying float v_depth;

void main() {
    v_color = a_color;
    v_texCoord0 = a_texCoord0;
    gl_Position = u_projViewTrans * a_position;

    v_depth = gl_Position.z / u_cameraFar;
}
