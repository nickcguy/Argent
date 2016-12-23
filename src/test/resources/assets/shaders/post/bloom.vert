#version 330

in vec4 a_position;
in vec2 a_texCoord0;

out vec2 TexCoords;

uniform mat4 u_projTrans;

void main() {
    TexCoords = a_texCoord0;
    gl_Position = u_projTrans * a_position;
}
