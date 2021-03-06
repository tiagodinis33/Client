#version 330

layout(location = 0) in vec3 pos;
layout(location = 1) in vec2 texCoords;
out vec2 texCoord;
uniform mat4 camera;
uniform mat4 perspective;
uniform mat4 transform;

out vec3 FragPos;
void main(){
    texCoord = texCoords;
    FragPos = vec3(transform * vec4(pos, 1.0));
    gl_Position =  (perspective * camera * transform)  * vec4(pos,1.0f);

}