#version 450 core

in vec4 a_position;

out vec4 Colour;

uniform vec4 u_colour;
uniform mat4 u_worldTrans;
uniform mat4 u_projViewTrans;

void main() {
    Colour = u_colour;
    gl_Position = u_projViewTrans * (u_worldTrans * a_position);
}