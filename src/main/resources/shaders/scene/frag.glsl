#version 120

varying vec4 v_color;
varying vec2 v_texCoords0;
varying vec4 v_position;

uniform sampler2D u_diffuse;
uniform sampler2D u_depth;
uniform sampler2D u_normal;
uniform sampler2D u_uber;
uniform vec2 u_screenRes;

uniform sampler2D u_buffers;

uniform float u_exposure = 1.0;

void main() {
    vec2 texel = gl_FragCoord.xy;
    texel.xy /= u_screenRes.xy;

    vec4 texCol = texture2D(u_diffuse, texel);

    vec3 col = texCol.rgb;

//    vec4 normal = texture2D(u_normal, texel);

//    col.rgb *= normal.rgb;

    vec4 depth = texture2D(u_depth, texel);
    col.rgb *= 1-depth.r;

    vec4 uber = texture2D(u_uber, texel);

    vec4 finalCol = vec4(1.0);

    finalCol = uber;
//    finalCol.rgb *= normal.rgb;
    finalCol.rgb *= texCol.rgb;

    finalCol.rgb *= 1-depth.r;


    // HDR stuff

    vec3 hdrCol = finalCol.rgb;

    vec3 mapped = vec3(1.0) - exp(-hdrCol * u_exposure);

    const float gamma = 2.2;

    mapped = pow(mapped, vec3(1.0 / gamma));

    gl_FragColor = vec4(mapped, 1.0);
//    gl_FragColor = vec4(v_texCoords0.x, v_texCoords0.y, 0.0, 1.0);
}
