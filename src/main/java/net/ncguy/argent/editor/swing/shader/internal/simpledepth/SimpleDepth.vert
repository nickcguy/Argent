#version 120

attribute vec4 a_position;

uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;
uniform vec2 u_cameraNearFar;

varying float v_depth;

void main() {
    gl_Position = u_projViewTrans * (u_worldTrans * a_position);

    v_depth = gl_Position.z / u_cameraNearFar.y;
}