#version 450 core

layout (location = 0) out vec4 gPosition;
layout (location = 1) out vec4 gNormal;
layout (location = 2) out vec4 gDiffuse;
layout (location = 3) out vec4 gSpecular;
layout (location = 4) out vec4 gAmbient;
layout (location = 5) out vec4 gDisplacement;
layout (location = 6) out vec4 gDepth;
layout (location = 7) out vec4 gEmissive;

in vec2 TexCoords;
in vec4 Position;
in vec4 Normal;
in vec3 Colour;
in float Depth;

// DIFFUSE
uniform sampler2D u_smartDiffuse;
uniform vec2 u_smartDiffuse_Offset;
uniform vec2 u_smartDiffuse_Scale;

uniform sampler2D u_smartSpecular;
uniform vec2 u_smartSpecular_Offset;
uniform vec2 u_smartSpecular_Scale;

uniform sampler2D u_smartNormal;
uniform vec2 u_smartNormal_Offset;
uniform vec2 u_smartNormal_Scale;

uniform sampler2D u_smartAmbient;
uniform vec2 u_smartAmbient_Offset;
uniform vec2 u_smartAmbient_Scale;

uniform sampler2D u_smartDisplacement;
uniform vec2 u_smartDisplacement_Offset;
uniform vec2 u_smartDisplacement_Scale;

uniform sampler2D u_smartEmissive;
uniform vec2 u_smartEmissive_Offset;
uniform vec2 u_smartEmissive_Scale;

void main() {

    vec2 n_texCoords = mod((TexCoords * u_smartNormal_Scale) + u_smartNormal_Offset, 1.0);

    vec4 normal = texture(u_smartNormal, n_texCoords);
    normal = Normal * normal;

    // Store the fragment position vector in the first gbuffer texture
    gPosition = Position;
    // Also store the per-fragment normals into the gbuffer
    gNormal = vec4(normalize(normal).rgb, 1.0);

    vec2 d_texCoords = mod((TexCoords * u_smartDiffuse_Scale) + u_smartDiffuse_Offset, 1.0);

    // And the diffuse per-fragment color
    gDiffuse = texture(u_smartDiffuse, d_texCoords);
    gDiffuse.rgb *= Colour;


    vec2 s_texCoords = mod((TexCoords * u_smartSpecular_Scale) + u_smartSpecular_Offset, 1.0);

    // Store specular intensity in gAlbedoSpec's alpha component
    gSpecular = vec4(vec3(texture(u_smartSpecular, s_texCoords).r), 1.0);

    vec2 a_texCoords = mod((TexCoords * u_smartAmbient_Scale) + u_smartAmbient_Offset, 1.0);

    gAmbient = vec4(texture(u_smartAmbient, a_texCoords).rgb, 1.0);

    vec2 dis_texCoords = mod((TexCoords * u_smartDisplacement_Scale) + u_smartDisplacement_Offset, 1.0);
    gDisplacement = vec4(texture(u_smartDisplacement, dis_texCoords).rgb, 1.0);

    vec2 e_texCoords = mod((TexCoords * u_smartEmissive_Scale) + u_smartEmissive_Offset, 1.0);
    gEmissive = vec4(texture(u_smartEmissive, e_texCoords).rgb, 1.0);

    gDepth = vec4(vec3(1-Depth), 1.0);

}