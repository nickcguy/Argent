#version 450 core

in vec4 a_position;

uniform mat4 u_worldTrans;
uniform mat4 u_projViewTrans;

void main() {
    gl_Position = u_projViewTrans * (u_worldTrans * a_position);
}