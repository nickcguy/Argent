#version 120

attribute vec4 a_position;
attribute vec2 a_texCoord0;
attribute vec4 a_normal;

uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;
uniform mat4 u_lightTrans;
uniform mat4 u_normalMatrix;
uniform vec2 u_cameraNearFar;

varying vec2 v_texCoord0;
varying float v_intensity;
varying vec4 v_positionLightTrans;
varying vec4 v_position;
varying float v_depth;

void main() {
    v_texCoord0 = a_texCoord0;

    v_position = u_worldTrans * a_position;
    v_positionLightTrans = u_lightTrans * v_position;

    gl_Position = u_projViewTrans * a_position;

    v_depth = gl_Position.z / u_cameraNearFar.y;

    vec3 normal = normalize(u_normalMatrix * a_normal).xyz;
    v_intensity = 1.0;
    if(normal.y < 0.5){
        if(normal.x > 0.5 || normal.x < -0.5)
    		v_intensity *= 0.8;
   		if(normal.z > 0.5 || normal.z < -0.5)
   			v_intensity *= 0.6;
   	}
}