#version 450

layout(triangles) in;
layout(triangle_strip, max_vertices = 6) out;

uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;
uniform mat4 u_normalMatrix;

uniform float u_time;

in VS_OUT {
    vec3 Normal;
    vec2 TexCoords;
    float Depth;
    vec4 Position;
} gs_in[];
out VS_OUT {
    vec3 Normal;
    vec2 TexCoords;
    float Depth;
    vec4 Position;
} gs_out;

vec3 GetNormal() {
    vec3 a = vec3(gl_in[0].gl_Position) - vec3(gl_in[1].gl_Position);
    vec3 b = vec3(gl_in[2].gl_Position) - vec3(gl_in[1].gl_Position);
    return normalize(cross(a, b));
}

vec4 explode(vec4 position, vec3 normal) {
    float magnitude = 1.0f;
    vec3 direction = normal * ((sin(u_time) + 1.0f) / 2.0f) * magnitude;
    return position + vec4(direction, 0.0f);
}

void main() {

    vec3 normal = GetNormal();

    for(int i = 0; i < 3; i++) {
        gl_Position = explode(gl_in[i].gl_Position, normal);
//        gl_Position = gl_in[i].gl_Position;
        gs_out.Normal = gs_in[i].Normal;
        gs_out.TexCoords = gs_in[i].TexCoords;
        gs_out.Depth = gs_in[i].Depth;
        gs_out.Position = gs_in[i].Position;
        EmitVertex();
    }

    EndPrimitive();
}
