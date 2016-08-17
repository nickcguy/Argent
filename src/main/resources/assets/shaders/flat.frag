#version 330

in vec3 Colour;

void main() {
	gl_FragColor = vec4(Colour, 1.0);
}
