#version 450

layout (location = 0) out vec4 ltgPosition;
layout (location = 1) out vec4 ltgTextures;
layout (location = 2) out vec4 ltgLighting;
layout (location = 3) out vec4 ltgGeometry;
layout (location = 4) out vec4 ltgReflection;
layout (location = 5) out vec4 ltgEmissive;

uniform vec2 u_screenRes;

uniform sampler2D texNormal;
uniform sampler2D texDiffuse;
uniform sampler2D texSpcAmbDisRef;
uniform sampler2D texEmissive;
uniform sampler2D texPosition;
uniform sampler2D texModNormal;

uniform sampler2D u_previousFrame;

uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;

#define EMISSIVE_THRESHOLD 0.95

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
    vec4 Position;
    float Specular;
    float Ambient;
    float Shininess;
    float Reflectiveness;

    vec4 Lighting;
} material;

in Trans {
    mat4 ProjView;
    mat4 Proj;
    mat4 World;
} matrices;

in VS_OUT {
    vec3 Normal;
    vec2 TexCoords;
    float Depth;
    vec4 Position;
} gs_out;

vec4 Position;

const float rayStep = 0.25;
const float minRayStep = 0.1;
const float maxSteps = 20;
const float searchDist = 5;
const float searchDistInv = 0.2;
const int numBinarySearchSteps = 5;
const float maxDDepth = 1.0;
const float maxDDepthInv = 1.0;

const float reflectionSpecularFalloffExponent = 3.0;

vec3 mod289(vec3 x) { return x - floor(x * (1.0 / 289.0)) * 289.0; }
vec2 mod289(vec2 x) { return x - floor(x * (1.0 / 289.0)) * 289.0; }
vec3 permute(vec3 x) { return mod289(((x*34.0)+1.0)*x); }
/// An implementation of a 2D simplex noise
float snoise(vec2 v) {
	const vec4 C = vec4(0.211324865405187, 0.366025403784439, -0.577350269189626, 0.024390243902439);
	vec2 i  = floor(v + dot(v, C.yy) );
	vec2 x0 = v -   i + dot(i, C.xx);
	vec2 i1; i1 = (x0.x > x0.y) ? vec2(1.0, 0.0) : vec2(0.0, 1.0);
	vec4 x12 = x0.xyxy + C.xxzz;
	x12.xy -= i1;
	i = mod289(i);
	vec3 p = permute( permute( i.y + vec3(0.0, i1.y, 1.0 )) + i.x + vec3(0.0, i1.x, 1.0 ));
	vec3 m = max(0.5 - vec3(dot(x0,x0), dot(x12.xy,x12.xy), dot(x12.zw,x12.zw)), 0.0);
	m = m*m ; m = m*m ;
	vec3 x = 2.0 * fract(p * C.www) - 1.0;
	vec3 h = abs(x) - 0.5;
	vec3 ox = floor(x + 0.5);
	vec3 a0 = x - ox;
	m *= 1.79284291400159 - 0.85373472095314 * ( a0*a0 + h*h );
	vec3 g; g.x  = a0.x  * x0.x  + h.x  * x0.y; g.yz = a0.yz * x12.xz + h.yz * x12.yw;
	return 130.0 * dot(m, g);
}

float noise(vec3 v) {
	return snoise((v.xy+v.z)*10);
}

vec3 BinarySearch(vec3 dir, inout vec3 hitCoord, out float dDepth) {
    float depth;
    for(int i = 0; i < numBinarySearchSteps; i++) {
        depth = (material.Position * matrices.ProjView).z;
        dDepth = hitCoord.z - depth;
        if(dDepth > 0.0)
            hitCoord += dir;

        dir *= 0.5;
        hitCoord -= dir;
    }
    vec4 projectedCoord = matrices.Proj * vec4(hitCoord, 1.0);
    projectedCoord.xy /= projectedCoord.w;
    projectedCoord.xy = projectedCoord.xy * 0.5 + 0.5;
    return vec3(projectedCoord.xy, depth);
}

vec4 RayCast(vec3 dir, inout vec3 hitCoord, out float dDepth) {
    dir *= rayStep;
    float depth;
    for(int i = 0; i < maxSteps; i++) {
        hitCoord += dir;
        depth = (material.Position * matrices.ProjView).z;
        dDepth = hitCoord.z - depth;
        if(dDepth < 0.0)
            return vec4(BinarySearch(dir, hitCoord, dDepth), 1.0);
    }
    return vec4(0.0);
}

vec4 rainbow(float x) {
    float level = x * 2.0;
    float r, g, b;
    if (level <= 0) {
        r = g = b = 0;
    } else if (level <= 1) {
        r = mix(1, 0, level);
        g = mix(0, 1, level);
        b = 0;
    } else if (level > 1) {
        r = 0;
        g = mix(1, 0, level-1);
        b = mix(0, 1, level-1);
    }
    return vec4(r, g, b, 1);
}

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
//    vec3 reflectDir = reflect(-lightDir, normal);

    float spec = 0.0;
//    #define useBlinn
    #ifdef useBlinn
    vec3 halfwayDir = normalize(lightDir + viewDir);
    spec = pow(max(dot(normal, halfwayDir), 0.0), 16.0);
    #else
    vec3 reflectDir = reflect(-lightDir, normal);
    spec = pow(max(dot(viewDir, reflectDir), 0.0), 8.0);
    #endif

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

//#define CALIBRATE
vec4 CalculateSSR(vec2 Texel) {

    vec4 origColour = texture(u_previousFrame, Texel);
    vec3 worldStartingPos = gs_out.Position.xyz;
    vec3 normal = material.Normal.xyz;
    vec3 cameraToWorld = worldStartingPos.xyz - u_viewPos.xyz;
    float cameraToWorldDist = length(cameraToWorld);
    float scaleNormal = max(3.0, cameraToWorldDist*1.5);
    #ifndef CALIBRATE
        normal.x += noise(worldStartingPos)/ scaleNormal;
        normal.y += noise(worldStartingPos+100)/ scaleNormal;
    #endif
    normal *= material.Reflectiveness;
    vec3 cameraToWorldNorm = normalize(cameraToWorld);
    vec3 refl = normalize(reflect(cameraToWorldNorm, normal));

    #ifdef CALIBRATE
        if(dot(refl, cameraToWorldNorm) < 0)
            return vec4(1.0);
    #endif

    float cosAngle = abs(dot(normal, cameraToWorldNorm));
    float fact = 1 - cosAngle;
    fact = min(1, 1.38 - fact*fact);
    #ifndef CALIBRATE
        if(fact > 0.95) return origColour;
    #endif

    vec3 newPos;
    vec4 newTexel;
    float i = 0;
    vec3 rayTrace = worldStartingPos;
    float currentWorldDist, rayDist;
    float incr = 0.4;
    do {
        i += 0.05;
        rayTrace += refl * incr;
        incr *= 1.3;
        newTexel = matrices.ProjView * vec4(rayTrace, 1.0);
        newTexel /= newTexel.w;
        newPos = texture(texPosition, newTexel.xy/2.0+0.5).xyz;
        currentWorldDist = length(newPos - u_viewPos.xyz);
        rayDist = length(rayTrace - u_viewPos.xyz);
        if(newTexel.x > 1 || newTexel.x < -1 || newTexel.y > 1 || newTexel.y < -1) {
            fact = 1.0;
            break;
        }
    }while(rayDist < currentWorldDist);

    #ifdef CALIBRATE
        if(cameraToWorldDist > currentWorldDist) return vec4(1.0, 1.0, 0.0, 1.0);
        if(newTexel.x > 1 || newTexel.x < -1 || newTexel.y > 1 || newTexel.y < -1) return vec4(0.0, 0.0, 0.0, 1.0);
        if(newTexel.z > 1 || newTexel.z < -1) return vec4(1.0);
        return rainbow(i);
    #endif
    vec4 newColour = texture(u_previousFrame, newTexel.xy * 0.5 + 0.5);
    if(dot(refl, cameraToWorldNorm) < 0) fact = 1.0;
    else if(newTexel.x > 1 || newTexel.x < -1 || newTexel.y > 1 || newTexel.y < -1) fact = 1.0;
    else if(cameraToWorldDist > currentWorldDist) fact = 1.0;
    return origColour*fact + newColour*(1-fact);
}

float avg(vec3 rgb) {
    return (rgb.r + rgb.g + rgb.b) / 3;
}

vec3 getEmissive(vec3 rgb) {
    vec3 emi = vec3(0.0);
    if(rgb.r > EMISSIVE_THRESHOLD) emi.r = rgb.r;
    if(rgb.g > EMISSIVE_THRESHOLD) emi.g = rgb.g;
    if(rgb.b > EMISSIVE_THRESHOLD) emi.b = rgb.b;
    return emi;
}

void main() {

    Position = gs_out.Position;

    vec2 Texel = gl_FragCoord.xy / u_screenRes;

    ltgPosition = texture(texPosition, Texel);
//    Position = ltgPosition;

    vec4 nor = texture(texNormal, Texel);
    vec4 dif = texture(texDiffuse, Texel);
    vec4 emi = texture(texEmissive, Texel);
    vec4 sad = texture(texSpcAmbDisRef, Texel);
    vec4 pos = texture(texPosition, Texel);
    float spc = sad.r;
    float amb = sad.g;
    float dis = sad.b;
    float ref = sad.a;

    material.Normal = nor;
    material.Diffuse = dif;
    material.Emissive = emi;
    material.Specular = spc;
    material.Position = pos;
    material.Ambient = amb;
    material.Shininess = dis;
    material.Reflectiveness = ref;


    vec3 lighting = (dif * amb * spc).rgb;
//    vec3 lighting = vec3(0.0);
    vec3 viewDir = normalize(u_viewPos - Position.xyz);

//    vec3 normal = nor.rgb;
//    vec3 normal = texture(texModNormal, Texel).rgb;
    vec3 normal = normalize(gs_out.Normal);

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
//    lighting += emi.rgb;

    // ScreenSpace Reflection
//    vec3 viewPos = gs_out.Position.xyz;
//    vec3 reflectedVector = normalize(reflect(normalize(viewPos), normalize(nor.xyz)));
//    vec3 hitPos = viewPos;
//    float dDepth;
//    vec4 coords = RayCast(reflectedVector * max(minRayStep, -viewPos.z), hitPos, dDepth);
//    vec2 dCoords = abs(vec2(0.5) - coords.xy);
//    float screenEdgeFactor = clamp(1.0 - (dCoords.x + dCoords.y), 0.0, 1.0);
//
//    vec4 prev = texture(u_previousFrame, coords.xy);
//    prev *= ref;
//
//    ltgReflection = vec4(prev.rgb,
//        pow(material.Reflectiveness, reflectionSpecularFalloffExponent) *
//        screenEdgeFactor * clamp(-reflectedVector.z, 0.0, 1.0) *
//        clamp((searchDist - length(viewPos - hitPos)) * searchDistInv, 0.0, 1.0) * coords.w);

    material.Lighting = vec4(lighting, 1.0);


    ltgReflection = CalculateSSR(Texel) * material.Reflectiveness;

    ltgTextures = material.Lighting;

    ltgEmissive = vec4(getEmissive(ltgTextures.rgb), 1.0);
    ltgEmissive.rgb += getEmissive(emi.rgb);

    ltgGeometry = vec4(vec3(amb), 1.0);

}
