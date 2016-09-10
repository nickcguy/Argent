#version 450 core

layout(location = 0) out vec4 OutputColour;
layout(location = 1) out vec4 OutputColour_store;

uniform sampler2D ltgPosition;
uniform sampler2D ltgTextures;
uniform sampler2D ltgLighting;
uniform sampler2D ltgReflection;

uniform sampler2D ltgFinalColour;

uniform vec2 u_screenRes;

in VS_OUT {
    vec3 Normal;
    vec2 TexCoords;
    float Depth;
    vec4 Position;
} gs_out;

void main() {

    vec2 Texel = gl_FragCoord.xy / u_screenRes;

    vec4 pos = texture(ltgPosition, Texel);
    vec4 tex = texture(ltgFinalColour, Texel);

    float depth = pos.a;

    vec4 finalCol = tex;
//    finalCol += depth;

    vec4 post = vec4(finalCol);

	vec4 ref = texture(ltgReflection, Texel);
	post += ref;

	OutputColour = ref;

	OutputColour_store = finalCol;
}
