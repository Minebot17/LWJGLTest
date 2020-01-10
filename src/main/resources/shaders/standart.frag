#version 330 core
layout(location = 0) out vec3 color;

const int lightMaxCount = 8;
const vec2 poissonDisk[4] = vec2[](
	vec2( -0.94201624, -0.39906216 ),
	vec2( 0.94558609, -0.76890725 ),
	vec2( -0.094184101, -0.92938870 ),
	vec2( 0.34495938, 0.29387760 )
);
in GS_OUT {
	vec3 fragmentColor;
	vec2 uv;
	vec3 normal_cameraspace;
	vec3 lightDirection_tangentspace[lightMaxCount];
	vec3 eyeDirection_tangentspace;
	mat3 tbn;
	vec4 shadowCoord[lightMaxCount];
} fs_in;

uniform sampler2D textureSampler;
uniform sampler2D normalSampler;
uniform sampler2D specularSampler;

uniform sampler2D shadowSampler[lightMaxCount];
uniform vec3 lightColor[lightMaxCount];
uniform float lightPower[lightMaxCount];
uniform float time;
uniform int lightCount;

struct LightData {
	float cosAlpha;
	float cosTheta;
	float visibility;
};

LightData calculateLight(int index, vec3 n, vec3 e) {
	LightData result = LightData(0, 0, 0);
	vec3 l = normalize(fs_in.lightDirection_tangentspace[index]);
	vec3 r = reflect(-l, n);
	result.cosAlpha = clamp(dot(e, r), 0, 1);
	result.cosTheta = clamp(dot(n, l), 0, 1);
	result.visibility = 1.0;

	float bias = 0.005*tan(acos(result.cosTheta)); // cosTheta is dot( n,l ), clamped between 0 and 1
	bias = clamp(bias, 0,0.01);
	//result.cosTheta = 1.0;

	for (int i=0;i<4;i++)
		if (texture(shadowSampler[index], fs_in.shadowCoord[index].xy + poissonDisk[i]/1000.0).r < fs_in.shadowCoord[index].z-bias-0.005)
			result.visibility-=0.25;
	return result;
}

void main(){
	vec3 n = normalize(texture(normalSampler, fs_in.uv).rgb*2.0 - 1.0);
	vec3 e = normalize(fs_in.eyeDirection_tangentspace);
	vec3 originalMaterialColor = texture(textureSampler, fs_in.uv).rgb;
	vec3 materialColor = originalMaterialColor;
	vec3 specularColor = texture(specularSampler, fs_in.uv).rgb;

	vec3 materialColors[lightMaxCount];
	vec3 specularColors[lightMaxCount];
	for (int i = 0; i < lightCount; i++){
		LightData data = calculateLight(i, n, e);
		materialColors[i] = materialColor * lightColor[i] * lightPower[i] * data.visibility * data.cosTheta;
		specularColors[i] = specularColor * lightColor[i] * lightPower[i] * data.visibility * pow(data.cosAlpha, 5);
	}
	if (lightCount != 0){
		materialColor = materialColors[0];
		specularColor = specularColors[0];
		for (int i = 1; i < lightCount; i++)
			materialColor = vec3(materialColor.r + materialColors[i].r, materialColor.g + materialColors[i].g, materialColor.b + materialColors[i].b);
		for (int i = 1; i < lightCount; i++)
		specularColor = vec3(specularColor.r + specularColors[i].r, specularColor.g + specularColors[i].g, specularColor.b + specularColors[i].b);
	}
	materialColor = clamp(materialColor, 0.0, 1.0);
	specularColor = clamp(specularColor, 0.0, 1.0);

	color = vec3(0.025, 0.025, 0.025) * originalMaterialColor + materialColor + specularColor;

	//float a = texture(shadowSampler, shadowCoord.xy).r;
	//color = vec3(a, a, a);
	//color = vec3(1., 0., 0.);
}