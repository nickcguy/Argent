#version 120

varying vec4 v_color;
varying vec2 v_texCoords0;
varying vec4 v_position;
varying vec4 v_worldPos;

uniform sampler2D u_diffuseTexture;
uniform vec4 u_diffuseColor;
uniform vec2 u_screenRes;
uniform float u_cameraFar;

void main() {
    vec4 col = texture2D(u_diffuseTexture, v_texCoords0);
    col.r = 1-(v_worldPos.z / u_cameraFar);
    col.gba = vec3(1.0);
    gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);
}
