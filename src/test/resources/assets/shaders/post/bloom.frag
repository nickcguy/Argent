#version 330

layout (location = 0) out vec4 gOut;

in vec2 TexCoords;

uniform sampler2D u_image;
uniform bool u_horizontal;

uniform float weight[5] = float[](0.227027, 0.1945946, 0.1216216, 0.054054, 0.016216);

void main() {
    vec2 TexelSize = 1.0 / textureSize(u_image, 0);
    vec3 result = texture(u_image, TexCoords).rgb * weight[0];
    if(u_horizontal) {
        for(int i = 1; i < 5; i++) {
            result += texture(u_image, TexCoords + vec2(TexelSize.x * i, 0.0)).rgb * weight[i];
            result += texture(u_image, TexCoords - vec2(TexelSize.x * i, 0.0)).rgb * weight[i];
        }
    }else{
        for(int i = 1; i < 5; i++) {
            result += texture(u_image, TexCoords + vec2(0.0, TexelSize.y * i)).rgb * weight[i];
            result += texture(u_image, TexCoords - vec2(0.0, TexelSize.y * i)).rgb * weight[i];
        }
    }
    gOut = vec4(result, 1.0);
}
