#version 330 core

layout (location = 0) out vec4 gPosition;
layout (location = 1) out vec4 gNormal;
layout (location = 2) out vec4 gAlbedoSpec;
layout (location = 3) out vec4 gButts;

in vec2 TexCoords;
in vec4 Position;
in vec3 Normal;
in vec3 Colour;

uniform sampler2D texture_diffuse1;
uniform sampler2D texture_specular1;

void main()
{
    // Store the fragment position vector in the first gbuffer texture
    gPosition = Position;
    // Also store the per-fragment normals into the gbuffer
    gNormal = vec4(normalize(Normal), 1.0);
    // And the diffuse per-fragment color
    gAlbedoSpec.rgb = texture(texture_diffuse1, TexCoords).rgb;
    gAlbedoSpec.rgb *= Colour;
    // Store specular intensity in gAlbedoSpec's alpha component
    gAlbedoSpec.a = texture(texture_specular1, TexCoords).r;

    gButts = vec4(1.0, 0.0, 1.0, 1.0);
}