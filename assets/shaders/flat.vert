#version 330

in vec4 a_position;
in vec4 a_color;

uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;

uniform vec4 u_smartDiffuseCol;

out vec3 Colour;

void main() {
    Colour = u_smartDiffuseCol.rgb;
    gl_Position = u_projViewTrans * (u_worldTrans * a_position);
}
