#version 450

layout (location = 0) out vec4 texNormal;
layout (location = 1) out vec4 texDiffuse;
layout (location = 2) out vec4 texSpcAmbDisRef;
layout (location = 3) out vec4 texEmissive;
layout (location = 4) out vec4 texPosition;
layout (location = 5) out vec4 texModNormal;

in vec4 Position;
in vec3 Normal;
in vec2 TexCoords;
uniform sampler2D u_texture2;
vec4 texture2_colour;
vec2 texture2_uv;

uniform sampler2D u_texture1;
vec4 texture1_colour;
vec2 texture1_uv;

uniform sampler2D u_texture3;
vec4 texture3_colour;
vec2 texture3_uv;

vec2 PannedTexCoords0;
uniform float u_pannerOffset0;

uniform sampler2D u_texture4;
vec4 texture4_colour;
vec2 texture4_uv;

void main() { 
	// Single-use fragments, typically used for texture sampling
		
	PannedTexCoords0 = mod((u_pannerOffset0 * vec2(1.0, 1.0)) + TexCoords, 1.0);
texture1_colour = texture(u_texture1, PannedTexCoords0);
	texture2_colour = texture(u_texture2, TexCoords);
	texture3_colour = texture(u_texture3, TexCoords);
	texture4_colour = texture(u_texture4, TexCoords);

	texDiffuse = texture2_colour;

	texNormal = texture3_colour;

	float internal_specular = texture4_colour.r;
	float internal_ambient = texture4_colour.g;
	float internal_displacement = texture4_colour.b;
	float internal_reflection = texture4_colour.g;

	texSpcAmbDisRef = vec4(internal_specular, internal_ambient, internal_displacement, internal_reflection);

	texEmissive = vec4(vec3(0.0), 1.0);

	texPosition = vec4(Position.xyz, 1.0);
	vec3 norm = Normal * texNormal.rgb;
	texModNormal = vec4(norm, 1.0);
}
