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
    if(gl_FrontFacing)
    {
        if (material.tex.enabled == 1){
            fragColor = texture(material.tex.slot, texCoord);
        } else {
            fragColor = material.color;
        }
    }
    else {
        fragColor = vec4(1.0f, 0.0f, 0.0f, 1.0f);
    }
}