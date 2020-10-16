#version 330
struct Texture{
    int enabled;
    sampler2D slot;
};
struct Material{
    vec4 color;
    Texture tex;
};
struct Light{
    vec3 color;
    vec3 lightPos;
    float attenuation;
};
out vec4 fragColor;

in vec4 color;
in vec2 texCoord;
in vec3 normals;
uniform Light lightsPos[20];
uniform int lightN;
in vec3 FragPos;
uniform vec3 viewPos;

uniform Material material;
void main(){
        vec4 color;
        if (material.tex.enabled == 1){
            color = texture(material.tex.slot, texCoord);
        } else {
            color = material.color;
        }
        vec3 result = vec3(0.0f,0.0f,0.0f);
        for(int i = 0; i< lightN; i++){
            vec3 norm = normalize(normals);
            vec3 lightDir = normalize(lightsPos[i].lightPos - FragPos);
            float diff = max(dot(norm, lightDir), 0.0);
            vec3 diffuse = diff * lightsPos[i].color;
            float specularStrength = lightsPos[i].attenuation;
            vec3 viewDir = normalize(viewPos - FragPos);
            vec3 reflectDir = reflect(-lightDir, norm);
            float spec = pow(max(dot(viewDir,reflectDir),0.0f),32);
            vec3 specular = specularStrength*spec*lightsPos[i].color;
            result += (/*ambient*/vec3(0.1f,0.1,0.1f) + /*diffuse lighning*/diffuse + /*Specular lightning*/ specular)*color.xyz;

        }
        fragColor = vec4(result, color.w);

}