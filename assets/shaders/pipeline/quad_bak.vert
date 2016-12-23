#version 450 core

in vec4 a_color;
in vec4 a_position;
in vec2 a_texCoord0;

uniform mat4 u_projTrans;

out VS_OUT {
    vec3 Normal;
    vec2 TexCoords;
    vec4 Position;
    vec4 Color;
} gs_out;

void main() {
    gs_out.Color = a_color;
    gs_out.Position = a_position;
    gs_out.TexCoords = a_texCoord0;
    gl_Position = gs_out.Position = u_projTrans * a_position;
}