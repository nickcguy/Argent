#version 330

in vec4 a_position;
in vec4 a_color;

uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;

out VS_OUT {
    vec3 Normal;
    vec2 TexCoords;
    vec4 Position;
    vec4 Color;
} gs_out;

void main() {
    gs_out.Position = u_worldTrans * a_position;
    gl_Position = u_projViewTrans * gs_out.Position;
}
