#version 120

attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;

uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;

varying vec4 v_color;
varying vec2 v_texCoords0;
varying vec4 v_position;

void main() {
    v_color = a_color;
    v_texCoords0 = a_texCoord0;
    v_position = a_position;
        gl_Position = u_projViewTrans * (u_worldTrans * a_position);
}
