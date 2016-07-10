#version 120


#ifndef LIGHT_COUNT
#define LIGHT_COUNT 0
#endif // #ifndef NULL

attribute vec4 a_position;

uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;

uniform vec4 u_lightPos[LIGHT_COUNT];

varying float v_depth;

void main() {
    gl_Position = u_projViewTrans * (u_worldTrans * a_position);

    float depth = 0;

    for(int i = 0; i < LIGHT_COUNT; i++) {
        vec4 light = u_lightPos[i];
        depth *= distance(gl_Position.xyz, light.xyz) / light.w;
    }
    v_depth = depth;
}