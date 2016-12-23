#version 450

in vec4 a_position;
in vec3 a_normal;
in vec2 a_texCoord0;

uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;
uniform mat3 u_normalMatrix;
uniform vec2 u_cameraNearFar;

out VS_OUT {
    vec3 Normal;
    vec2 TexCoords;
    float Depth;
    vec4 Position;
} vsOut;

void main() {

	vec4 Position = u_worldTrans * a_position;
	gl_Position = u_projViewTrans * Position;

	vsOut.TexCoords = a_texCoord0;
    vsOut.Position = Position;
    vsOut.Normal = normalize(u_normalMatrix * a_normal);

	vsOut.Depth = gl_Position.z / u_cameraNearFar.y;
}
