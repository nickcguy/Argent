#version 120

attribute vec4 a_position;

uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;

varying vec4 v_position;

void main() {
    v_position = u_projViewTrans * (u_worldTrans * a_position);
    gl_Position = v_position;
}