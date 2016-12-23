#version 450

/* This comes interpolated from the vertex shader */
in VS_OUT {
    vec3 Normal;
    vec2 TexCoords;
    float Depth;
    vec4 Position;
} vsOut;

/* The texture we are going to sample */
uniform sampler2D tex;
uniform vec2 u_screenRes;

out vec4 color;

void main(void) {
    vec2 Texel = gl_FragCoord.xy / u_screenRes;
    /* Well, simply sample the texture */
    color = texture(tex, Texel);
}
