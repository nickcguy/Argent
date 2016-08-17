#version 450 core

layout (location = 0) out vec4 ltgPosition;
layout (location = 1) out vec4 ltgTextures;

uniform vec2 u_screenRes;

uniform sampler2D texNormal;
uniform sampler2D texDiffuse;
uniform sampler2D texSpecular;
uniform sampler2D texAmbient;
uniform sampler2D texDisplacement;
uniform sampler2D texEmissive;
uniform sampler2D texReflection;
uniform sampler2D texPosition;

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

    vec2 Texel = gl_FragCoord.xy / u_screenRes;

    ltgPosition = texture(texPosition, Texel);
    vec3 Position = ltgPosition.xyz;

    vec4 nor = texture(texNormal, Texel);
    vec4 dif = texture(texDiffuse, Texel);
    vec4 emi = texture(texEmissive, Texel);
    float spc = texture(texSpecular, Texel).r;
    float amb = texture(texAmbient, Texel).r;

    vec3 lighting = (dif * amb * spc).rgb;
    vec3 viewDir = normalize(u_viewPos - Position.xyz);

    if(spc > 1.0) {
        ltgTextures.rgb = vec3(1.0, 0.0, 1.0);
        ltgTextures.a = 1.0;
        return;
    }

    for(int i = 0; i < NR_LIGHTS; i++) {
        Light l = lights[i];
        float distance = length(l.Position - Position.xyz);
        float lMax = max(max(l.Colour.r, l.Colour.g), l.Colour.b);
        l.Radius = sqrt(l.Linear * l.Linear - 4 * l.Quadratic * (1.0 - (256.0/5.0) * lMax));
        if(distance < l.Radius) {
            // Light diffuse
            vec3 lightDir = normalize(l.Position - Position.xyz);
            vec3 diffuse = max(dot(nor.rgb, lightDir), 0.0) * dif.rgb * l.Colour;

            // Light specular
            vec3 halfwayDir = normalize(lightDir + viewDir);
            float spec = pow(max(dot(nor.rgb, halfwayDir), 0.0), 16.0);
            vec3 specular = l.Colour * spec * spc;

            // Light attenuation
            float attenuation = 1.0 / (1.0 + l.Linear * distance + l.Quadratic * distance * distance);
            diffuse *= attenuation;
            specular *= attenuation;
            lighting += (diffuse + specular) * l.Intensity;

            if(inRange(Position.xyz, l.Position, 0.1))
                emi.rgb = vec3(1.0);

        }else{
            lighting -= 0.001;
        }
    }

    // Emissive
    lighting += emi.rgb;

    ltgTextures = vec4(lighting, 1.0);

}
