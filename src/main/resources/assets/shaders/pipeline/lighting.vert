#version 450 core

in vec4 a_position;
in vec3 a_normal;
in vec2 a_texCoord0;

uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;
uniform vec2 u_cameraNearFar;

uniform vec3 u_viewPos;

out VertexData {
    vec2 TexCoord;
    vec3 Normal;
} VertexOut;

void main() {

    VertexOut.TexCoord = a_texCoord0;
    VertexOut.Normal = a_normal;

    vec4 Position = (u_worldTrans * a_position);
	gl_Position = u_projViewTrans * Position;
}
