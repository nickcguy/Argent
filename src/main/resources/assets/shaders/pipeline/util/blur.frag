#version 450 core

layout(location = 0) out vec4 FragColorEmissive;
layout(location = 1) out vec4 FragColor1;

in vec2 TexCoords;
in vec4 Color;

uniform sampler2D image;

uniform bool horizontal;
in vec2 blurTexCoords[14];
uniform int blurIteration = 1;

float weight[5] = float[] (0.227027, 0.1945946, 0.1216216, 0.054054, 0.016216);

vec3 GaussianBlur(sampler2D tex, vec2 centerUV, vec2 halfPixelOffset, vec2 pixelOffset) {
    vec3 colOut = vec3(0.0);

    const int stepCount = 2;
    const float gWeights[stepCount] = {
        0.44908, 0.05092
    };
    const float gOffsets[stepCount] = {
        0.53805, 2.06278
    };

    for(int i = 0; i < stepCount; i++) {
        vec2 texCoordOffset = gOffsets[i] * pixelOffset;
        vec3 col = texture(tex, centerUV + texCoordOffset).rgb + texture(tex, centerUV - texCoordOffset).rgb;
        colOut += gWeights[i] * col;
    }

    return colOut;
}

void blurImage(sampler2D tex, vec2 tex_offset, inout vec4 result) {

    for(int i = 0; i < blurIteration; i++)
        result.rgb += GaussianBlur(tex, TexCoords, tex_offset/2, tex_offset);

//    if(horizontal) {
//        for(int i = 1; i < 5; i++) {
//            result += texture(tex, TexCoords + vec2(tex_offset.x * i, 0.0)).rgba * weight[i];
//            result += texture(tex, TexCoords - vec2(tex_offset.x * i, 0.0)).rgba * weight[i];
//        }
//    }else{
//        for(int i = 1; i < 5; i++) {
//            result += texture(tex, TexCoords + vec2(0.0, tex_offset.y * i)).rgba * weight[i];
//            result += texture(tex, TexCoords - vec2(0.0, tex_offset.y * i)).rgba * weight[i];
//        }
//    }
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

    emissive.a = 1.0;
    FragColorEmissive = emissive;

}