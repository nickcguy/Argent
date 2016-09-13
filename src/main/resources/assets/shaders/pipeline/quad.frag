#version 450 core

layout(location = 0) out vec4 OutputColour;
layout(location = 1) out vec4 OutputColour_store;
layout(location = 2) out vec4 OutputColour_Blur;

uniform sampler2D ltgPosition;
uniform sampler2D ltgTextures;
uniform sampler2D ltgLighting;
uniform sampler2D ltgReflection;
uniform sampler2D ltgEmissive;

uniform sampler2D ltgFinalColour;

uniform vec2 u_screenRes;

uniform float u_exposure = 1.0;
uniform float u_gamma = 1.0;

in VS_OUT {
    vec3 Normal;
    vec2 TexCoords;
    vec4 Position;
    vec4 Color;
} gs_out;

void main() {

    vec2 Texel = gl_FragCoord.xy / u_screenRes;
//    vec2 Texel = gs_out.TexCoords;
//    Texel.y = 1-Texel.y;

    vec4 pos = texture(ltgPosition, Texel);
    vec4 tex = texture(ltgFinalColour, Texel);

    float depth = pos.a;

    vec4 finalCol = tex;
    finalCol += 1-depth;

    vec4 post = vec4(finalCol);

	vec4 ref = texture(ltgReflection, Texel);
	post.rgb += ref.rgb;

	vec4 emi = texture(ltgEmissive, Texel);
	finalCol += emi;
	post += emi;

	vec3 result = vec3(1.0) - exp(-post.rgb * u_exposure);
	result = pow(result, vec3(1.0 / u_gamma));

	OutputColour = vec4(result, 1.0);

	OutputColour_store = finalCol;

	OutputColour_Blur = emi;
}
