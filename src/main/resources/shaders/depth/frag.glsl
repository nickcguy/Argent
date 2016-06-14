#version 120

uniform sampler2D u_depthMapDir;
uniform samplerCube u_depthMapCube;
uniform int u_type;
uniform vec3 u_cameraPosition;
uniform vec3 u_lightPosition;
uniform vec2 u_cameraNearFar;
uniform float u_cameraFar;

varying float v_depth;
varying vec2 v_texCoord0;
varying float v_intensity;
varying vec4 v_positionLightTrans;
varying vec4 v_position;

void main() {

    float intensity = 0.0;
    vec3 lightDirection = v_position.xyz-u_lightPosition;
    float lenToLight = length(u_lightPosition)/u_cameraFar;

//    lenToLight = 1-lenToLight;

    vec4 depthMap = vec4(0.0);
    float lenDepthMap = -1.0;

    if(u_type == 1) {   // SPOT / DIRECTIONAL
        vec3 depth = (v_positionLightTrans.xyz / v_positionLightTrans.w)*0.5+0.5;
        if(v_positionLightTrans.z>=0.0 && (depth.x >= 0.0) && (depth.x <= 1.0) && (depth.y >= 0.0) && (depth.y <= 1.0)) {
            depthMap = texture2D(u_depthMapDir, depth.xy);
        }
    }else if(u_type == 2) { // POINT
        depthMap = textureCube(u_depthMapCube, lightDirection);
    }
    lenDepthMap = depthMap.r;

    if(lenDepthMap > lenToLight-0.005) {
        intensity = 0.1;
    }else{
//        intensity = (0.5*(1.0-lenToLight));
        intensity = lenDepthMap;
    }


    gl_FragColor = vec4(vec3(1-intensity), 1.0);
//    gl_FragColor = depthMap.rgba;
}
