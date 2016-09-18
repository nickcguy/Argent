#version 450

out vec4 FragColour;

uniform vec4 u_colour = vec4(0.0, 0.714, 0.586, 1.0);

void main() {
	FragColour = u_colour;
}
