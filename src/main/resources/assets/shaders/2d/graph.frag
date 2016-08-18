#version 450 core

out vec4 FinalColour;

in vec4 Position;
in vec4 Color;
in vec2 TexCoords;

uniform sampler2D u_texture;

void main() {
    FinalColour = texture(u_texture, TexCoords);
}