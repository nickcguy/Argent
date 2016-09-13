#version 450

out vec4 OutputColour;

in VS_OUT {
    vec3 Normal;
    vec2 TexCoords;
    float Depth;
    vec4 Position;
} vsOut;

uniform sampler2D u_quadBuffer;
uniform vec2 u_screenRes;

void main() {

    vec2 Texel = gl_FragCoord.xy / u_screenRes;
    vec4 quad = texture(u_quadBuffer, Texel);

	OutputColour = quad;
}
