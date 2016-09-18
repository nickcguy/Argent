#version 450 core

out vec4 color;

uniform sampler2D depthMap;
uniform vec2 u_screenRes;

in VS_OUT {
    vec4 FragPos;
    vec3 Normal;
    vec2 TexCoords;
    vec4 FragPosLightSpace;
    float Depth;
} vs_out;

void main() {
    color = vec4(vec3(vs_out.Depth), 1.0);
}