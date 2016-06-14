#version 120

attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;

uniform mat4 u_projViewTrans;

varying vec4 v_color;
varying vec2 v_texCoords0;
varying vec4 v_position;
varying vec4 v_worldPos;

void main() {
    v_color = a_color;
    v_texCoords0 = a_texCoord0;
    v_position = a_position;
    v_worldPos = u_projViewTrans * a_position;
    gl_Position = v_worldPos;
}
