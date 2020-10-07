#version 330

layout(location = 0) in vec3 pos;
layout(location = 1) in vec2 texCoords;
layout (location = 2) in vec3 normalIn;
out vec3 normals;
out vec2 texCoord;
out vec3 FragPos;
uniform mat4 camera;
uniform mat4 perspective;
uniform mat4 transform;
void main(){
    texCoord = texCoords;
    normals = normalIn;
    FragPos = vec3(transform * vec4(pos, 1.0));
    gl_Position =  (perspective * camera * transform)  * vec4(pos,1.0f);

}