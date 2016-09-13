#version 450 core

layout(location = 0) out vec4 FragColorEmissive;
layout(location = 1) out vec4 FragColor1;

in vec2 TexCoords;
in vec4 Color;

uniform sampler2D image;

uniform bool horizontal;

float weight[5] = float[] (0.227027, 0.1945946, 0.1216216, 0.054054, 0.016216);

void blurImage(sampler2D tex, vec2 tex_offset, inout vec4 result) {
    if(horizontal) {
        for(int i = 1; i < 5; i++) {
            result += texture(tex, TexCoords + vec2(tex_offset.x * i, 0.0)).rgba * weight[i];
            result += texture(tex, TexCoords - vec2(tex_offset.x * i, 0.0)).rgba * weight[i];
        }
    }else{
        for(int i = 1; i < 5; i++) {
            result += texture(tex, TexCoords + vec2(0.0, tex_offset.y * i)).rgba * weight[i];
            result += texture(tex, TexCoords - vec2(0.0, tex_offset.y * i)).rgba * weight[i];
        }
    }
}

void scaleWeights(float scalar) {
    for(int i = 0; i < 5; i++)
        weight[i] *= scalar;
}

void main() {

//    scaleWeights(1.01);

    vec2 tex_offset = 1.0 / textureSize(image, 0);
    vec4 emissive = texture(image, TexCoords).rgba * weight[0];

    blurImage(image, tex_offset, emissive);

    FragColorEmissive = emissive;

}