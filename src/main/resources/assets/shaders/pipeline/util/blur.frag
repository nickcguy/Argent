#version 450 core

layout(location = 0) out vec4 FragColor0;
layout(location = 1) out vec4 FragColor1;
in vec2 TexCoords;
in vec4 Color;

uniform sampler2D image;

uniform bool horizontal;

uniform float weight[5] = float[] (0.227027, 0.1945946, 0.1216216, 0.054054, 0.016216);

void main() {
    vec2 tex_offset = 1.0 / textureSize(image, 0);
    vec4 result = texture(image, TexCoords).rgba * weight[0];


    if(horizontal) {
        for(int i = 1; i < 5; i++) {
            result += texture(image, TexCoords + vec2(tex_offset.x * i, 0.0)).rgba * weight[i];
            result += texture(image, TexCoords - vec2(tex_offset.x * i, 0.0)).rgba * weight[i];
        }
    }else{
        for(int i = 1; i < 5; i++) {
            result += texture(image, TexCoords + vec2(0.0, tex_offset.y * i)).rgba * weight[i];
            result += texture(image, TexCoords - vec2(0.0, tex_offset.y * i)).rgba * weight[i];
        }
    }

    FragColor0 = result;
//    FragColor0 = texture(image, TexCoords);

}