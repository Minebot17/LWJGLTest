#version 330 core
layout(location = 0) in vec3 vertexPosition_modelspace;
layout(location = 1) in vec2 vertexUV;
layout(location = 2) in vec3 vertexNormal;
layout(location = 3) in vec3 vertexTangent;
layout(location = 4) in vec3 vertexBitangent;

const int lightMaxCount = 8;
uniform mat4 mvp;
uniform mat4 model;
uniform mat4 view;
uniform mat3 mv3x3;
uniform vec3 lightPosition_worldspace[lightMaxCount];
uniform mat4 depthBiasMVP[lightMaxCount];
uniform int lightcount;

out VS_OUT {
	vec3 fragmentColor;
	vec2 uv;
	vec3 normal_cameraspace;
	vec3 lightDirection_tangentspace[lightMaxCount];
	vec3 eyeDirection_tangentspace;
	mat3 tbn;
	vec4 shadowCoord[lightMaxCount];
} vs_out;

void main(){
	vs_out.uv = vertexUV;
	gl_Position = mvp * vec4(vertexPosition_modelspace, 1);

	// Vector that goes from the vertex to the camera, in camera space.
	vec3 vertexPosition_cameraspace = (view * model * vec4(vertexPosition_modelspace, 1)).xyz;
	vec3 lightDirection_cameraspace[lightMaxCount];
	for (int i = 0; i < lightcount; i++)
		lightDirection_cameraspace[i] = (view * vec4(lightPosition_worldspace[i] - (model * vec4(vertexPosition_modelspace, 1)).xyz, 0)).xyz;

	vec3 eyeDirection_cameraspace = -vertexPosition_cameraspace;

	// Normal of the the vertex, in camera space
	vs_out.normal_cameraspace = (view * model * vec4(vertexNormal, 0)).xyz; // Only correct if ModelMatrix does not scale the model ! Use its inverse transpose if not

	vec3 vertexNormal_cameraspace = mv3x3 * normalize(vertexNormal);
    vec3 vertexTangent_cameraspace = mv3x3 * normalize(vertexTangent);
    vec3 vertexBitangent_cameraspace = mv3x3 * normalize(vertexBitangent);

	vs_out.tbn = transpose(mat3(
        vertexTangent_cameraspace,
        vertexBitangent_cameraspace,
        vertexNormal_cameraspace
    )); // You can use dot products instead of building this matrix and transposing it. See References for details.

	for (int i = 0; i < lightcount; i++)
		vs_out.lightDirection_tangentspace[i] = vs_out.tbn * lightDirection_cameraspace[i];

    vs_out.eyeDirection_tangentspace =  vs_out.tbn * eyeDirection_cameraspace;

	for (int i = 0; i < lightcount; i++)
		vs_out.shadowCoord[i] = depthBiasMVP[i] * vec4(vertexPosition_modelspace,1);
}