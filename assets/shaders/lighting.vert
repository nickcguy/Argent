#version 330

in vec4 a_position;
in vec3 a_normal;
in vec2 a_texCoord0;

uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;

out vec2 TexCoords;

void main() {
    TexCoords = a_texCoord0;
	gl_Position = u_projViewTrans * (u_worldTrans * a_position);
}
