#version 450

layout(location = 0) out vec4 FragColour;

in vec4 Colour;

void main() {
	FragColour = 1-Colour;
}
