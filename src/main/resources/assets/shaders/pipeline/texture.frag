#version 450 core

layout (location = 0) out vec4 texNormal;
layout (location = 1) out vec4 texDiffuse;
layout (location = 2) out vec4 texSpcAmbDis;
layout (location = 3) out vec4 texEmissive;
layout (location = 4) out vec4 texReflection;
layout (location = 5) out vec4 texPosition;
layout (location = 6) out vec4 texModNormal;


in VS_OUT {
    vec3 Normal;
    vec2 TexCoords;
    float Depth;
    vec4 Position;
} gs_out;

vec3 Normal;
vec2 TexCoords;
vec4 Position;
float Depth;

uniform sampler2D u_smartNormal;
uniform vec2 u_smartNormal_Offset;
uniform vec2 u_smartNormal_Scale;

uniform sampler2D u_smartDiffuse;
uniform vec2 u_smartDiffuse_Offset;
uniform vec2 u_smartDiffuse_Scale;

uniform sampler2D u_smartSpecular;
uniform vec2 u_smartSpecular_Offset;
uniform vec2 u_smartSpecular_Scale;

uniform sampler2D u_smartAmbient;
uniform vec2 u_smartAmbient_Offset;
uniform vec2 u_smartAmbient_Scale;

uniform sampler2D u_smartDisplacement;
uniform vec2 u_smartDisplacement_Offset;
uniform vec2 u_smartDisplacement_Scale;

uniform sampler2D u_smartEmissive;
uniform vec2 u_smartEmissive_Offset;
uniform vec2 u_smartEmissive_Scale;

uniform sampler2D u_smartReflection;
uniform vec2 u_smartReflection_Offset;
uniform vec2 u_smartReflection_Scale;

uniform vec4 u_smartDiffuse_Col;
uniform vec4 u_smartSpecular_Col;
uniform vec4 u_smartAmbient_Col;
uniform vec4 u_smartEmissive_Col;
uniform vec4 u_smartReflection_Col;
uniform vec4 u_smartAmbientLight_Col;
uniform vec4 u_smartFog_Col; // TODO support fog

uniform float u_smartBlending;
// TODO add support for blending attribute

void main() {

    Normal = gs_out.Normal;
    TexCoords = gs_out.TexCoords;
    Position = gs_out.Position;
    Depth = gs_out.Depth;

    vec2 normal_texCoords       = mod((TexCoords * u_smartNormal_Scale)         + u_smartNormal_Offset,         1.0);
    vec2 diffuse_texCoords      = mod((TexCoords * u_smartDiffuse_Scale)        + u_smartDiffuse_Offset,        1.0);
    vec2 specular_texCoords     = mod((TexCoords * u_smartSpecular_Scale)       + u_smartSpecular_Offset,       1.0);
    vec2 ambient_texCoords      = mod((TexCoords * u_smartAmbient_Scale)        + u_smartAmbient_Offset,        1.0);
    vec2 displacement_texCoords = mod((TexCoords * u_smartDisplacement_Scale)   + u_smartDisplacement_Offset,   1.0);
    vec2 emissive_texCoords     = mod((TexCoords * u_smartEmissive_Scale)       + u_smartEmissive_Offset,       1.0);
    vec2 reflection_texCoords   = mod((TexCoords * u_smartReflection_Scale)     + u_smartReflection_Offset,     1.0);

    // Normal
    vec4 normal = texture(u_smartNormal, normal_texCoords);
    texNormal = vec4(Normal * normal.rgb, normal.a);
    normal.rgb = normalize(normal.rgb * 2.0 - 1.0);
//    normal.rgb = Normal * normal.rgb;
//normalize(normal.rgb * 2.0 - 1.0)
    texModNormal = vec4(normal.rgb, 1.0);

    // Diffuse
    texDiffuse = texture(u_smartDiffuse, diffuse_texCoords);
    texDiffuse *= u_smartDiffuse_Col;
//    texDiffuse.a = u_smartBlending;

    // Specular
    texSpcAmbDis.r = texture(u_smartSpecular, specular_texCoords).r;
    texSpcAmbDis.r *= u_smartSpecular_Col.r;

    // Ambient
    texSpcAmbDis.g = texture(u_smartAmbient, ambient_texCoords).g;
    texSpcAmbDis.g *= u_smartAmbient_Col.g;
//    texAmbient += (1-u_smartAmbientLight_Col);

    // Displacement
    texSpcAmbDis.b = texture(u_smartDisplacement, displacement_texCoords).b;

    // Emissive
    texEmissive = texture(u_smartEmissive, emissive_texCoords);
    texEmissive *= u_smartEmissive_Col;

    // Reflection
    texReflection = texture(u_smartReflection, reflection_texCoords);
    texReflection *= u_smartReflection_Col;

    // Position
    texPosition = Position;
    texPosition.a = 1-Depth;

}
