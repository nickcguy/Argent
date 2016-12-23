#version 450

in vec4 a_position;
in vec3 a_normal;
in vec2 a_texCoord0;

uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;
uniform mat3 u_normalMatrix;
uniform vec2 u_cameraNearFar;

out vec3 Normal;
out vec2 TexCoords;
out float Depth;
out vec4 Position;

void main() {

	Position = u_worldTrans * a_position;
	gl_Position = u_projViewTrans * Position;

	TexCoords = a_texCoord0;
    Normal = normalize(u_normalMatrix * a_normal);

	Depth = gl_Position.z / u_cameraNearFar.y;
}
