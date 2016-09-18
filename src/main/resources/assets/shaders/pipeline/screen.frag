#version 450

out vec4 OutputColour;

in VS_OUT {
    vec3 Normal;
    vec2 TexCoords;
    float Depth;
    vec4 Position;
} vsOut;

uniform sampler2D u_quadBuffer;
uniform sampler2D u_toolBuffer;
uniform vec2 u_screenRes;

void main() {

    vec2 Texel = gl_FragCoord.xy / u_screenRes;
    vec4 quad = texture(u_quadBuffer, Texel);
    vec4 tool = texture(u_toolBuffer, Texel);

    if((tool.r > 0.0 && tool.r < 1.0) && (tool.g > 0.0 && tool.g < 1.0) && (tool.b > 0.0 && tool.b < 1.0))
        quad = tool;

	OutputColour = quad;
}
