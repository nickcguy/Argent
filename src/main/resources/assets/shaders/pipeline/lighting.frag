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

struct PointLight {
    vec3 Position;
    vec3 Colour;

    vec3 Ambient;
    vec3 Specular;

    float Linear;
    float Quadratic;
    float Intensity;
};
const int NR_LIGHTS = 32;
uniform PointLight lights[NR_LIGHTS];
uniform vec3 u_viewPos;

struct Material {
    vec4 Normal;
    vec4 Diffuse;
    vec4 Emissive;
    float Specular;
    float Ambient;
    float Shininess;
} material;

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

float Attenuate(float constant, float distance, float linear, float quadratic) {
    return 1.0f / (constant + linear * distance + quadratic * (distance * distance));
}

vec3 CalcPointLight(PointLight l, vec3 normal, vec3 fragPos, vec3 viewDir) {
    vec3 lightDir = normalize(l.Position - fragPos);
    float diff = max(dot(normal, lightDir), 0.0);
    vec3 reflectDir = reflect(-lightDir, normal);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.Shininess);
    float distance = length(l.Position - fragPos);
    //Attenuate(l.Intensity, distance, l.Linear, l.Quadratic)
    float attenuation = 1.0 / (1.0 + l.Linear * distance + l.Quadratic * (distance * distance));

    attenuation *= l.Intensity;

    vec3 ambient  = l.Ambient * material.Ambient;
    vec3 diffuse  = l.Colour * diff * material.Diffuse.rgb;
    vec3 specular = l.Specular * spec * material.Specular;
    ambient  *= attenuation;
    diffuse  *= attenuation;
    specular *= attenuation;
    return (ambient + diffuse + specular);
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

    material.Normal = nor;
    material.Diffuse = dif;
    material.Emissive = emi;
    material.Specular = spc;
    material.Ambient = amb;
    material.Shininess = dis;


    vec3 lighting = (dif * amb * spc).rgb;
//    vec3 lighting = vec3(0.0);
    vec3 viewDir = normalize(u_viewPos - Position.xyz);

//    vec3 normal = nor.rgb;
//    vec3 normal = texture(texModNormal, Texel).rgb;
    vec3 normal = normalize(nor.rgb);

    if(spc > 1.0) {
        ltgTextures.rgb = dif.rgb;
        ltgTextures.a = 1.0;
        return;
    }

    ltgLighting = vec4(0.0, 0.0, 0.0, 1.0);

    for(int i = 0; i < NR_LIGHTS; i++) {
        vec3 lightDiff = CalcPointLight(lights[i], normal, Position.xyz, viewDir);
        lighting += lightDiff;
        ltgLighting.rgb += lightDiff;
    }



    // Emissive
    lighting += emi.rgb;

    ltgTextures = vec4(lighting, 1.0);

    ltgGeometry = vec4(vec3(amb), 1.0);

}
