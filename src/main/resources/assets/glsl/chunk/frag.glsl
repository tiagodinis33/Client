#version 330

out vec4 fragColor;
in vec4 color;
in vec2 texCoord;
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

        if (material.tex.enabled == 1){
            fragColor = texture(material.tex.slot, texCoord);
        } else {
            fragColor = color;
        }

    }
