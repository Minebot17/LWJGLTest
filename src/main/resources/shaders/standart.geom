#version 330 core
layout (triangles) in;
layout (triangle_strip, max_vertices = 3) out;

const int lightMaxCount = 8;
in VS_OUT {
	vec3 fragmentColor;
	vec2 uv;
	vec3 normal_cameraspace;
	vec3 lightDirection_tangentspace[lightMaxCount];
	vec3 eyeDirection_tangentspace;
	mat3 tbn;
	vec4 shadowCoord[lightMaxCount];
} gs_in[];

out GS_OUT {
	vec3 fragmentColor;
	vec2 uv;
	vec3 normal_cameraspace;
	vec3 lightDirection_tangentspace[lightMaxCount];
	vec3 eyeDirection_tangentspace;
	mat3 tbn;
	vec4 shadowCoord[lightMaxCount];
} gs_out;

uniform float time;

void main() {    
	for (int i = 0; i < 3; i++) {
		gl_Position = gl_in[i].gl_Position; 
		gs_out.fragmentColor = gs_in[i].fragmentColor;
		gs_out.uv = gs_in[i].uv;
		gs_out.normal_cameraspace = gs_in[i].normal_cameraspace;
		gs_out.lightDirection_tangentspace = gs_in[i].lightDirection_tangentspace;
		gs_out.eyeDirection_tangentspace = gs_in[i].eyeDirection_tangentspace;
		gs_out.tbn = gs_in[i].tbn;
		gs_out.shadowCoord = gs_in[i].shadowCoord;
		EmitVertex();
	}

    EndPrimitive();
}  