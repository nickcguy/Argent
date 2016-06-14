#version 120

varying vec4 v_color;
varying vec2 v_texCoords0;
varying vec4 v_position;

uniform sampler2D u_diffuseTexture;
uniform vec4 u_diffuseColor;
uniform vec2 u_screenRes;

void main() {


    vec3 texCol = texture2D(u_diffuseTexture, v_texCoords0).rgb;

    texCol *= u_diffuseColor.rgb;

    vec3 col = v_position.xyz;
    col /= 600;

    col *= texCol;

    col.r = clamp(col.r, 0.1, 1.0);
    col.g = clamp(col.g, 0.1, 1.0);
    col.b = clamp(col.b, 0.1, 1.0);

    gl_FragColor = vec4(col, 1.0);
}
