#version 450 core

in vec4 a_position;
in vec3 a_normal;
in vec4 a_tangent;
in vec4 a_binormal;
in vec2 a_texCoord0;

uniform mat4 u_projViewTrans;
uniform mat4 u_projTrans;
uniform mat4 u_worldTrans;
uniform vec2 u_cameraNearFar;

uniform vec3 u_viewPos;

out Trans {
    mat4 ProjView;
    mat4 Proj;
    mat4 World;
} matrices;

out VS_OUT {
    vec3 Normal;
    vec2 TexCoords;
    float Depth;
    vec4 Position;
} gs_out;


void main() {

    matrices.ProjView = u_projViewTrans;
    matrices.Proj = u_projTrans;
    matrices.World = u_worldTrans;

    gs_out.TexCoords = a_texCoord0;
    gs_out.Normal = a_normal;
    gs_out.Position = (u_worldTrans * a_position);
	gl_Position = u_projViewTrans * gs_out.Position;

	gs_out.Depth = gl_Position.z / u_cameraNearFar.y;

}
