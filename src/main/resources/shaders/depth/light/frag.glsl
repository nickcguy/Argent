#version 120

struct Light {
    vec4 colour;
    vec3 direction;
    vec3 position;
    float intensity;
    float cutoffAngle;
    float exponent;
};

uniform Light u_light = Light(vec4(1.0), vec3(0.0), vec3(0.0), 0.0, 0.0, 0.0);

void main() {

}
