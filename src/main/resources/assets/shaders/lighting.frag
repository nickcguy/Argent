#version 410 core
#extension GL_ARB_explicit_uniform_location : enable

out vec4 FragColor;
in vec2 TexCoords;

uniform vec2 u_screenRes;

layout(location = 4) uniform sampler2D bufPosition;
layout(location = 5) uniform sampler2D bufNormal;
layout(location = 6) uniform sampler2D bufDiffuse;
layout(location = 7) uniform sampler2D bufSpecular;

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

uniform float ambient = 1;

vec4 debug(vec2 point) {
    vec4 col = vec4(vec3(1.0), 1.0);
    if(point.x > .501) {
        if(point.y > .501) {
            col.rgb = vec3(1.0, 0.0, 0.0);
        }else if(point.y < .499){
            col.rgb = vec3(1.0, 1.0, 0.0);
        }
    }else if(point.x < .499){
        if(point.y > .501) {
            col.rgb = vec3(0.0, 1.0, 0.0);
        }else if(point.y < .499){
            col.rgb = vec3(0.0, 1.0, 1.0);
        }
    }
    return col;
}

void main() {

    vec2 Texel = gl_FragCoord.xy / u_screenRes;

    vec3 Position = texture(bufPosition, Texel).rgb;
    vec3 Normal = texture(bufNormal, Texel).rgb;
    vec3 Diffuse = texture(bufDiffuse, Texel).rgb;
    float Specular = texture(bufSpecular, Texel).r;

    vec3 lighting = Diffuse * ambient * Specular;
    vec3 viewDir = normalize(u_viewPos - Position);

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

    vec4 fragColor = vec4(lighting, 1);

//    fragColor = debug(Texel);

    FragColor = fragColor;

}