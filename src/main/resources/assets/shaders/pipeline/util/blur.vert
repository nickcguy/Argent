#version 450 core

in vec4 a_color;
in vec4 a_position;
in vec2 a_texCoord0;

uniform mat4 u_projTrans;
uniform bool horizontal;

out vec4 Color;
out vec4 Position;
out vec2 TexCoords;
uniform sampler2D image;


out vec2 blurTexCoords[14];

void main() {
    Color = a_color;
    Position = a_position;
    TexCoords = vec2(a_texCoord0.x, 1-a_texCoord0.y);
    gl_Position = Position = u_projTrans * a_position;

    vec2 tex_offset = 1.0 / textureSize(image, 0);

//    if(horizontal) {
//        blurTexCoords[0] = TexCoords  + vec2(-tex_offset.x*7, 0.0);
//        blurTexCoords[1] = TexCoords  + vec2(-tex_offset.x*6, 0.0);
//        blurTexCoords[2] = TexCoords  + vec2(-tex_offset.x*5, 0.0);
//        blurTexCoords[3] = TexCoords  + vec2(-tex_offset.x*4, 0.0);
//        blurTexCoords[4] = TexCoords  + vec2(-tex_offset.x*3, 0.0);
//        blurTexCoords[5] = TexCoords  + vec2(-tex_offset.x*2, 0.0);
//        blurTexCoords[6] = TexCoords  + vec2(-tex_offset.x*1, 0.0);
//        blurTexCoords[7] = TexCoords  + vec2( tex_offset.x*1, 0.0);
//        blurTexCoords[8] = TexCoords  + vec2( tex_offset.x*2, 0.0);
//        blurTexCoords[9] = TexCoords  + vec2( tex_offset.x*3, 0.0);
//        blurTexCoords[10] = TexCoords + vec2( tex_offset.x*4, 0.0);
//        blurTexCoords[11] = TexCoords + vec2( tex_offset.x*5, 0.0);
//        blurTexCoords[12] = TexCoords + vec2( tex_offset.x*6, 0.0);
//        blurTexCoords[13] = TexCoords + vec2( tex_offset.x*7, 0.0);
//    }else{
//        blurTexCoords[0] = TexCoords  + vec2(0.0, -tex_offset.y*7);
//        blurTexCoords[1] = TexCoords  + vec2(0.0, -tex_offset.y*6);
//        blurTexCoords[2] = TexCoords  + vec2(0.0, -tex_offset.y*5);
//        blurTexCoords[3] = TexCoords  + vec2(0.0, -tex_offset.y*4);
//        blurTexCoords[4] = TexCoords  + vec2(0.0, -tex_offset.y*3);
//        blurTexCoords[5] = TexCoords  + vec2(0.0, -tex_offset.y*2);
//        blurTexCoords[6] = TexCoords  + vec2(0.0, -tex_offset.y*1);
//        blurTexCoords[7] = TexCoords  + vec2(0.0,  tex_offset.y*1);
//        blurTexCoords[8] = TexCoords  + vec2(0.0,  tex_offset.y*2);
//        blurTexCoords[9] = TexCoords  + vec2(0.0,  tex_offset.y*3);
//        blurTexCoords[10] = TexCoords + vec2(0.0,  tex_offset.y*4);
//        blurTexCoords[11] = TexCoords + vec2(0.0,  tex_offset.y*5);
//        blurTexCoords[12] = TexCoords + vec2(0.0,  tex_offset.y*6);
//        blurTexCoords[13] = TexCoords + vec2(0.0,  tex_offset.y*7);
//    }
}