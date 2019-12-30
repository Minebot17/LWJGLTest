#version 330 core
layout(location = 0) in vec3 asd;

uniform mat4 mvp;

void main() {
    gl_Position = mvp * vec4(asd, 1.0);
}
