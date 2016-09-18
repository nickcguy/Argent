#version 450

in vec4 a_position;
in vec3 a_normal;
in vec2 a_texCoord0;

uniform mat4 u_projViewTrans;
uniform mat4 u_projTrans;
uniform mat4 u_worldTrans;
uniform vec2 u_cameraNearFar;
uniform mat4 lightSpaceMatrix;

out VS_OUT {
    vec4 FragPos;
    vec3 Normal;
    vec2 TexCoords;
    vec4 FragPosLightSpace;
    float Depth;
} vs_out;

void main() {
    vs_out.FragPos = (u_worldTrans * a_position);
    gl_Position = u_projViewTrans * vs_out.FragPos;

    vs_out.Depth = 1-((u_projViewTrans * vs_out.FragPos).z / u_cameraNearFar.y);

    vs_out.Normal = transpose(inverse(mat3(u_worldTrans))) * a_normal;
    vs_out.TexCoords = a_texCoord0;
    vs_out.FragPosLightSpace = lightSpaceMatrix * vec4(vs_out.FragPos.xyz, 1.0);
}