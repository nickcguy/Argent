#version 330 core

layout (location = 0) out vec4 gPosition;
layout (location = 1) out vec4 gNormal;
layout (location = 2) out vec4 gDiffuse;
layout (location = 3) out vec4 gSpecular;

in vec2 TexCoords;
in vec4 Position;
in vec4 Normal;
in vec3 Colour;

uniform sampler2D u_smartDiffuse;
uniform sampler2D u_smartSpecular;
uniform sampler2D u_smartNormal;

void main() {

    vec4 normal = texture(u_smartNormal, TexCoords);
    normal = Normal * normal;

    // Store the fragment position vector in the first gbuffer texture
    gPosition = Position;
    // Also store the per-fragment normals into the gbuffer
    gNormal = vec4(normalize(normal).rgb, 1.0);
    // And the diffuse per-fragment color
    gDiffuse = texture(u_smartDiffuse, TexCoords);
    gDiffuse.rgb *= Colour;
    // Store specular intensity in gAlbedoSpec's alpha component
    gSpecular = vec4(vec3(texture(u_smartSpecular, TexCoords).r), 1.0);

}