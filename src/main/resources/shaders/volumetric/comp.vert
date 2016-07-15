#version 120

attribute vec4 a_position;
attribute vec4 a_normal;

uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;
uniform mat4 u_normalMatrix;

uniform mat4 u_lightTrans;
uniform vec2 u_cameraNearFar;

varying vec4 v_position;
varying vec4 v_positionLightTrans;
varying float v_intensity;
varying float v_depth;

void main() {
//    vec4 pos = (u_worldTrans * a_position);
//    v_position = u_projViewTrans * pos;
//    v_positionLightTrans = u_lightTrans * a_position;
//    gl_Position = v_position;

    vec4 pos = (u_worldTrans * a_position);

    v_position = u_projViewTrans * pos;
    v_positionLightTrans = u_lightTrans * pos;
    gl_Position = v_position;
}