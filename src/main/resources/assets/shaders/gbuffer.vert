#version 120

attribute vec4 a_position;
attribute vec3 a_normal;
attribute vec4 a_color;
attribute vec2 a_texCoord0;

uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;

varying out vec4 Position;
varying out vec3 Normal;
varying out vec3 Colour;
varying out vec2 TexCoords;

void main() {
    Colour = a_color.rgb;
    TexCoords = a_texCoord0;
    Position = (u_worldTrans * a_position);
    Normal = a_normal;
	gl_Position = u_projViewTrans * Position;
}
