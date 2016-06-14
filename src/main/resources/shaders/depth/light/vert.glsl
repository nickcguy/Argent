#version 120

attribute vec4 a_position;

uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;
uniform mat4 u_lightTrans;
uniform float u_cameraFar;

uniform vec4 u_cameraPosition;
uniform vec2 u_cameraNearFar;

varying vec4 v_position;
varying float v_depth;

void main() {
    v_position = u_worldTrans * a_position;
    gl_Position = u_projViewTrans * v_position;

    v_depth = (gl_Position.z-u_cameraPosition.z) / (u_cameraNearFar.y/2);
}
