#version 330 core

#define PI 3.14159265359
#define NR_LIGHTS LIGHT_COUNT

out vec4 FragColour;

in vec2 TexCoords;
in vec4 Position;
in vec3 WorldPos;
in vec3 Normal;

uniform vec4 u_cameraPosition;

uniform vec3 albedo;
uniform float metallic;
uniform float roughness;
uniform float ao;

struct Light {
    vec3 Position;
    vec3 Colour;
};
uniform Light lights[NR_LIGHTS];


float DistributionGGX(vec3 N, vec3 H, float roughness);
float GeometrySchlickGGX(float NdotV, float roughness);
float GeometrySmith(vec3 N, vec3 V, vec3 L, float roughness);
vec3 fresnelSchlickRoughness(float cosTheta, vec3 F0, float roughness);


void main() {

    vec3 N = normalize(Normal);
    vec3 V = normalize(u_cameraPosition.xyz - WorldPos);

    vec3 F0 = vec3(0.04);
    F0 = mix(F0, albedo, metallic);
    vec3 F = fresnelSchlickRoughness(max(dot(N, V), 0.0), F0, roughness);

    vec3 kS = F;
    vec3 kD = vec3(1.0) - kS;
    kD *= 1.0 - metallic;

    // Reflectance equation
    vec3 Lo = vec3(0.0);
    for(int i = 0; i < NR_LIGHTS; i++) {
        Light l = lights[i];

        // Calculate per-light radiance
        vec3 L = normalize(l.Position - WorldPos);
        vec3 H = normalize(V + L);
        float distance = length(l.Position - WorldPos);
        float attenuation = 1.0 / distance * distance;
        vec3 radiance = l.Colour * attenuation;

        // Cook-torrance brdf
        float NDF = DistributionGGX(N, H, roughness);
        float G = GeometrySmith(N, V, L, roughness);

        vec3 nominator = NDF * G * F;
        float denominator = 4 * max(dot(V, N), 0.0) * max(dot(L, N), 0.0) + 0.001;
        vec3 brdf = nominator / denominator;

        // Add to outgoing radiance Lo
        float NdotL = max(dot(N, L), 0.0);
        Lo += (kD * albedo / PI + brdf) * radiance * NdotL;
    }

    vec3 ambient = vec3(0.03) * albedo * ao;
    vec3 colour = ambient + Lo;

    colour = colour / (colour + vec3(1.0));
    colour = pow(colour, vec3(1.0 / 2.2));

	FragColour = vec4(colour, 1.0);
}


float DistributionGGX(vec3 N, vec3 H, float roughness) {
    float a      = roughness*roughness;
    float a2     = a*a;
    float NdotH  = max(dot(N, H), 0.0);
    float NdotH2 = NdotH*NdotH;

    float nom   = a2;
    float denom = (NdotH2 * (a2 - 1.0) + 1.0);
    denom = PI * denom * denom;

    return nom / denom;
}

float GeometrySchlickGGX(float NdotV, float roughness) {
    float r = (roughness + 1.0);
    float k = (r*r) / 8.0;

    float nom   = NdotV;
    float denom = NdotV * (1.0 - k) + k;

    return nom / denom;
}

float GeometrySmith(vec3 N, vec3 V, vec3 L, float roughness) {
    float NdotV = max(dot(N, V), 0.0);
    float NdotL = max(dot(N, L), 0.0);
    float ggx2  = GeometrySchlickGGX(NdotV, roughness);
    float ggx1  = GeometrySchlickGGX(NdotL, roughness);

    return ggx1 * ggx2;
}

vec3 fresnelSchlickRoughness(float cosTheta, vec3 F0, float roughness) {
    return F0 + (max(vec3(1.0 - roughness), F0) - F0) * pow(1.0 - cosTheta, 5.0);
}