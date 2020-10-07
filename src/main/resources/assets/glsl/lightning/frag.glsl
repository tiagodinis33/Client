#version 330

out vec4 fragColor;
in vec4 color;
in vec2 texCoord;
in vec3 normals;
uniform vec3 lightPos;
in vec3 FragPos;
struct Texture{
    int enabled;
    sampler2D slot;
};
struct Material{
    vec4 color;
    Texture tex;
};

uniform Material material;
void main(){
    if(gl_FrontFacing)
    {
        vec4 color;
        if (material.tex.enabled == 1){
            color = texture(material.tex.slot, texCoord);
        } else {
            color = material.color;
        }
        vec3 norm = normalize(normals);
        vec3 lightDir = normalize(lightPos - FragPos);
        float diff = max(dot(norm, lightDir), 0.0);
        vec3 diffuse = diff * vec3(1.0f,1.0f,1.0f);
        vec3 result = (vec3(0.5f,0.5f,0.5f) + diffuse) * color.xyz;
        fragColor = vec4(result, color.w);
    }
    else {
        fragColor = vec4(1.0f, 0.0f, 0.0f, 1.0f);
    }
}