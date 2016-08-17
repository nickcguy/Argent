#version 450 core

out vec4 OutputColour;

uniform sampler2D ltgPosition;
uniform sampler2D ltgTextures;
uniform sampler2D ltgLighting;

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
    vec4 tex = texture(ltgTextures, Texel);

    float depth = pos.a;

    vec4 finalCol = tex;
//    finalCol += depth;

	OutputColour = finalCol;
}
