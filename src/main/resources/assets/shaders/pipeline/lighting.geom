#version 450

layout(triangles) in;
layout(triangle_strip, max_vertices = 6) out;

uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;
uniform mat4 u_normalMatrix;

in VertexData {
    vec2 TexCoord;
    vec3 Normal;
} VertexIn[];

out VertexData {
    vec2 TexCoord;
    vec3 Normal;
} VertexOut;

void main() {

    for(int i = 0; i < gl_in.length(); i++) {
        gl_Position = gl_in[i].gl_Position;
        VertexOut.Normal = (VertexIn[i].Normal);
        VertexOut.TexCoord = VertexIn[i].TexCoord;
        EmitVertex();
    }
    EndPrimitive();

    for(int i = 0; i < gl_in.length(); i++) {
        gl_Position = gl_in[i].gl_Position + (vec4(5.0, 0.0, 0.0, 0.0));
        VertexOut.Normal = (VertexIn[i].Normal);
        VertexOut.TexCoord = VertexIn[i].TexCoord;
        EmitVertex();
    }
    EndPrimitive();
}
