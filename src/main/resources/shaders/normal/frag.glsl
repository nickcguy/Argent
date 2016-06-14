#version 120

varying vec4 v_color;
varying vec4 v_position;
varying vec3 v_normals;
varying vec2 v_texCoords;

uniform sampler2D u_normalTexture;
uniform sampler2D u_normalMap;
uniform vec2 u_screenRes;

uniform sampler2D u_buffers;

void main() {

    vec4 normalMap = texture2D(u_normalTexture, v_texCoords);

    gl_FragColor = vec4(normalMap.rgb, 1.0);
}
