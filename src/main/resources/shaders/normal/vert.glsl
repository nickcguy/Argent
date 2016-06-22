#version 120

attribute vec4 a_position;
attribute vec3 a_normal;
attribute vec2 a_texCoord0;

uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;
uniform mat3 u_normalMatrix;

varying vec4 v_position;
varying vec3 v_normals;
varying vec2 v_texCoords;

void main() {
    v_position = a_position;
        gl_Position = u_projViewTrans * (u_worldTrans * a_position);

    v_texCoords = a_texCoord0;

    v_normals = normalize(u_normalMatrix * a_normal);
    v_normals *= 2.0;
    v_normals -= 1.0;
}
