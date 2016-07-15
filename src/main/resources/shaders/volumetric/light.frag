#version 120

uniform vec2 u_cameraNearFar;
uniform vec4 u_cameraPosition;
uniform vec2 u_viewportSize;

varying vec4 v_position;

bool inRad(vec2 texel, vec2 center, float rad) {

    float len = distance(texel, center);

//    if(texel.x > center.x-rad && texel.x < center.x+rad) {
//        if(texel.y > center.y-rad && texel.y < center.y+rad) {
//            return true;
//        }
//    }
    return len < rad;
}

void main() {

    vec3  vlen = v_position.xyz-(u_cameraPosition.xyz);
    float dist = length(vlen);
//    float dist = distance(v_position.xyz, u_cameraPosition.xyz);
    float rad = 10;
    float factor = 0.0;

    vec3 debugCol = vec3(0.0);

    vec2 texel = gl_FragCoord.xy / 1024;
    vec2 center = vec2(0.5);


    if(inRad(texel, center, 0.2)) {
        factor = 1-distance(texel, center);
    } else if(inRad(texel, center, 0.3)) {
        factor = 1-distance(texel, center);
    }else{
        factor = 0.0;
//        debugCol.r = texel.x;
//        debugCol.g = texel.y;
//        debugCol.b = texel.x/texel.y;
    }

    factor = 1-(distance(texel, center)*3);

    vec4 finalCol;

//	finalCol = vec4(vec3(length(v_position.xyz-u_cameraPosition.xyz)/u_cameraNearFar.y), 1.0);
    finalCol = vec4(factor);


	gl_FragColor = finalCol;
}
