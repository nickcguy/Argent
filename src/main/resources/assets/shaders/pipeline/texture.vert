#version 450

in vec4 a_position;
in vec3 a_normal;
in vec2 a_texCoord0;

uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;
uniform mat4 u_normalMatrix;
uniform vec2 u_cameraNearFar;

out vec4 Normal;
out vec2 TexCoords;
out float Depth;
out vec4 Position;

void main() {
    TexCoords = a_texCoord0;
    Position = (u_worldTrans * a_position);
    Normal = vec4(a_normal, 1.0);
	Position = u_projViewTrans * Position;
	gl_Position = Position;
	Position = a_position;

	Depth = gl_Position.z / u_cameraNearFar.y;
}
