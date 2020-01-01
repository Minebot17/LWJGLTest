#version 330 core
layout(location = 0) in vec3 pos;
layout(location = 1) in vec2 uv;

out vec2 tex;
uniform mat4 mvp;

void main() {
    tex = uv;
    gl_Position = mvp * vec4(pos, 1.0);
}