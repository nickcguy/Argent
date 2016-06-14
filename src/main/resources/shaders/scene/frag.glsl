#version 120

varying vec4 v_color;
varying vec2 v_texCoords0;
varying vec4 v_position;

uniform sampler2D u_diffuse;
uniform sampler2D u_depth;
uniform sampler2D u_normal;
uniform vec2 u_screenRes;

uniform sampler2D u_buffers;

void main() {
    vec2 texel = gl_FragCoord.xy;
    texel.xy /= u_screenRes.xy;

    vec4 texCol = texture2D(u_diffuse, texel);

    vec3 col = texCol.rgb;

    vec4 normal = texture2D(u_normal, texel);

    col.rgb *= normal.rgb;

    vec4 depth = texture2D(u_depth, texel);
    col.rgb *= 1.0-depth.r;

    gl_FragColor = vec4(col, 1.0);
//    gl_FragColor = vec4(v_texCoords0.x, v_texCoords0.y, 0.0, 1.0);
}
