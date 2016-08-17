#version 450 core

in vec4 a_position;

in vec4 a_tangent;
in vec4 a_binormal;

uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;
uniform vec2 u_cameraNearFar;

uniform vec3 u_viewPos;

out VS_OUT {
    vec3 Normal;
    vec2 TexCoords;
    float Depth;
    vec4 Position;
} vsOut;


void main() {

    vsOut.Position = (u_worldTrans * a_position);
	gl_Position = u_projViewTrans * vsOut.Position;
}
