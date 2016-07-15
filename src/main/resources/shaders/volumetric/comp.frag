#version 120
//#extension GL_NV_shadow_samplers_cube : enable

uniform sampler2D u_depthMapDir;
uniform samplerCube u_depthMapCube;
uniform vec2 u_cameraNearFar;
uniform vec4 u_cameraPosition;
uniform float u_type;

varying vec4 v_position;
varying vec4 v_positionLightTrans;

float intensity = 0.0;
float lenDepthMap = -1.0;

void main() {
	vec3 lightDirection = v_position.xyz - u_cameraPosition.xyz;
	float factor = u_cameraNearFar.y;
	float len = length(lightDirection);
	if(len > factor) factor = len;
	float lenToLight = len/factor;

	if(u_type == 1.0) {
	    vec3 depth = (v_positionLightTrans.xyz / v_positionLightTrans.w) * 0.5 + 0.5;
	    if (v_positionLightTrans.z>=0.0 && (depth.x >= 0.0) && (depth.x <= 1.0) && (depth.y >= 0.0) && (depth.y <= 1.0) ) {
	    	lenDepthMap = texture2D(u_depthMapDir, depth.xy).a;
	    }
	}else if(u_type == 2.0) {
	    lenDepthMap = textureCube(u_depthMapCube, lightDirection).a;
	}

	if(lenDepthMap > lenToLight-0.005) {
	    intensity = lenDepthMap;
	}else{
	    intensity = 0.5*(lenDepthMap);
	}

	gl_FragColor = vec4(vec3(intensity), 1.0);

}
