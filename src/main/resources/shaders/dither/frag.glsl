#version 120

varying vec4 v_color;
varying vec2 v_texCoords0;
varying vec4 v_position;

uniform sampler2D u_ditherMask;
uniform vec2 u_screenRes;
uniform float u_threshold = 0.5;

void main() {
    vec4 mask = texture2D(u_ditherMask, v_texCoords0);
    if(mask.a >= u_threshold)
        mask.rgb = vec3(1.0);
    else
        mask.rgb = vec3(0.0);
    gl_FragColor = vec4(mask.rgb, 1.0);
}
