#version 410 core

out vec4 FragColor;
in vec2 TexCoords;

uniform vec2 u_screenRes;
uniform float u_gamma = 1.0;
uniform float u_exposure = 1.0;

uniform sampler2D bufPosition;
uniform sampler2D bufNormal;
uniform sampler2D bufDiffuse;
uniform sampler2D bufSpecular;
uniform sampler2D bufAmbient;
uniform sampler2D bufDisplacement;
uniform sampler2D bufEmissive;
uniform sampler2D bufDepth;

// POST PROCESSING
uniform sampler2D postEmissive;
//uniform sampler2D postHDR;

struct Light {
    vec3 Position;
    vec3 Colour;
    float Linear;
    float Quadratic;
    float Intensity;
};
const int NR_LIGHTS = 32;
uniform Light lights[NR_LIGHTS];
uniform vec3 u_viewPos;

void main() {

    vec2 Texel = gl_FragCoord.xy / u_screenRes;

    vec3 Position = texture(bufPosition, Texel).rgb;
    vec3 Normal = texture(bufNormal, Texel).rgb;
    vec3 Diffuse = texture(bufDiffuse, Texel).rgb;
    vec3 Emissive = texture(bufEmissive, Texel).rgb;
    float Specular = texture(bufSpecular, Texel).r;
    float ambient = texture(bufAmbient, Texel).r;

    vec3 processedEmissive = texture(postEmissive, Texel).rgb;

    vec3 lighting = Diffuse * ambient * Specular;
    vec3 viewDir = normalize(u_viewPos - Position);

    if(Specular > 1.0) {
        FragColor = texture(bufDiffuse, Texel);
        return;
    }

    for(int i = 0; i < NR_LIGHTS; i++) {
        Light l = lights[i];
        // Light diffuse
        vec3 lightDir = normalize(l.Position - Position);
        vec3 diffuse = max(dot(Normal, lightDir), 0.0) * Diffuse * l.Colour;

        // Light specular
        vec3 halfwayDir = normalize(lightDir + viewDir);
        float spec = pow(max(dot(Normal, halfwayDir), 0.0), 16.0);
        vec3 specular = l.Colour * spec * Specular;

        // Light attenuation
        float distance = length(l.Position - Position);
        float attenuation = 1.0 / (1.0 + l.Linear * distance + l.Quadratic * distance * distance);
        diffuse *= attenuation;
        specular *= attenuation;
        lighting += (diffuse + specular) * l.Intensity;
    }

//    vec3 processedHDR = texture(postHDR, Texel).rgb;
//    vec3 mapped = lighting / (lighting + vec3(1.0));
    vec3 mapped = vec3(1.0) - exp(-lighting * u_exposure);
    mapped = pow(mapped, vec3(1.0 / u_gamma));

    float depth = texture(bufDepth, Texel).r;
    mapped += depth * 0.25;

    mapped += processedEmissive;

    vec4 fragColor = vec4(mapped, 1);

    // POST PROCESSING
//    fragColor = texture(postEmissive, Texel);

    FragColor = fragColor;

}