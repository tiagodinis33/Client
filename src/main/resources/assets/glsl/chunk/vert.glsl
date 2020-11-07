#version 330

layout(location = 0) in vec3 pos;
layout(location = 1) in vec2 texCoords;
layout(location = 2) in vec4 colors;
out vec2 texCoord;
out vec4 color;
uniform mat4 camera;
uniform mat4 perspective;
uniform mat4 transform;

void main(){
    color = colors;
    texCoord = texCoords;
    gl_Position =  (perspective * camera * transform)  * vec4(pos,1.0f);

}