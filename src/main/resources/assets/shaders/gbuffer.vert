#version 120

attribute vec4 a_position;
attribute vec3 a_normal;
attribute vec4 a_color;
attribute vec2 a_texCoord0;

uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;
uniform vec4 u_smartDiffuseCol;
uniform mat4 u_normalMatrix;

varying out vec4 Position;
varying out vec4 Normal;
varying out vec3 Colour;
varying out vec2 TexCoords;

void main() {
    Colour = u_smartDiffuseCol.rgb;
    TexCoords = a_texCoord0;
    Position = (u_worldTrans * a_position);
    Normal = vec4(a_normal, 1.0);
	gl_Position = u_projViewTrans * Position;
}
