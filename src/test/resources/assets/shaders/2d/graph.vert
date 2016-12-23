#version 450 core

in vec4 a_color;
in vec4 a_position;
in vec2 a_texCoord0;

uniform mat4 u_projTrans;

out vec4 Color;
out vec4 Position;
out vec2 TexCoords;

void main() {
    Color = a_color;
    Position = a_position;
    TexCoords = a_texCoord0;
    gl_Position = Position = u_projTrans * a_position;
}