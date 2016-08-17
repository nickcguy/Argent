#version 450

layout (location = 0) out vec4 ltgPosition;
layout (location = 1) out vec4 ltgTextures;
layout (location = 2) out vec4 ltgLighting;
layout (location = 3) out vec4 ltgGeometry;

uniform vec2 u_screenRes;

uniform sampler2D texNormal;
uniform sampler2D texDiffuse;
uniform sampler2D texSpcAmbDis;
uniform sampler2D texEmissive;
uniform sampler2D texReflection;
uniform sampler2D texPosition;
uniform sampler2D texModNormal;

uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;

struct Light {
    vec3 Position;
    vec3 Colour;
    float Linear;
    float Quadratic;
    float Intensity;
    float Radius;
};
const int NR_LIGHTS = 32;
uniform Light lights[NR_LIGHTS];
uniform vec3 u_viewPos;

in VS_OUT {
    vec3 Normal;
    vec2 TexCoords;
    float Depth;
    vec4 Position;
} gs_out;

vec4 Position;

bool inRange(vec3 a, vec3 b, float range) {
    if(a.x > b.x + range) return false;
    if(a.x < b.x - range) return false;

    if(a.y > b.y + range) return false;
    if(a.y < b.y - range) return false;

    if(a.z > b.z + range) return false;
    if(a.z < b.z - range) return false;

    return true;
}

void main() {

    Position = gs_out.Position;

    vec2 Texel = gl_FragCoord.xy / u_screenRes;

    ltgPosition = texture(texPosition, Texel);
//    Position = ltgPosition;

    vec4 nor = texture(texNormal, Texel);
    vec4 dif = texture(texDiffuse, Texel);
    vec4 emi = texture(texEmissive, Texel);
    vec4 sad = texture(texSpcAmbDis, Texel);
    float spc = sad.r;
    float amb = sad.g;
    float dis = sad.b;

    vec3 lighting = (dif * amb * spc).rgb;
    vec3 viewDir = normalize(u_viewPos - Position.xyz);

//    vec3 normal = nor.rgb;
    vec3 normal = texture(texModNormal, Texel).rgb;

    if(spc > 1.0) {
        ltgTextures.rgb = vec3(1.0, 0.0, 1.0);
        ltgTextures.a = 1.0;
        return;
    }

    ltgLighting = vec4(0.0, 0.0, 0.0, 1.0);

    for(int i = 0; i < NR_LIGHTS; i++) {
        Light l = lights[i];
//        l.Position = ((u_worldTrans * vec4(l.Position, 0.0))).xyz;
        float distance = length(l.Position - Position.xyz);
        float lMax = max(max(l.Colour.r, l.Colour.g), l.Colour.b);
        if(distance < l.Radius) {
            // Light diffuse
            vec3 lightDir = normalize(l.Position - Position.xyz);
            vec3 diffuse = max(dot(nor.rgb, lightDir), 0.0) * dif.rgb * l.Colour;

            // Light specular
            vec3 halfwayDir = normalize(lightDir + viewDir);
            float spec = pow(max(dot(normal, halfwayDir), 0.0), 16.0);
            vec3 specular = l.Colour * spec * spc;

            // Light attenuation
            float attenuation = 1.0 / (1.0 + l.Linear * distance + l.Quadratic * distance * distance);
            diffuse *= attenuation;
            specular *= attenuation;
            vec3 modified = (diffuse + specular) * l.Intensity;
            ltgLighting.rgb += modified.r;
            lighting += modified;

//            if(inRange(Position.xyz, l.Position, 0.1))
//                emi.rgb = vec3(1.0);

        }else{
            lighting -= 0.001;
        }
    }

    // Emissive
    lighting += emi.rgb;

    ltgTextures = vec4(lighting, 1.0);

    ltgGeometry = vec4(vec3(amb), 1.0);

}
