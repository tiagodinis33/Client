package org.hawkstudios.jogo.engine;

import org.joml.Vector4f;

public class Material {
    
    private Vector4f color = new Vector4f(1);
    private Texture texture = null; 
    public Vector4f getColor() {
        return color;
    }
    public void setColor(Vector4f color) {
        this.color = color;
    }
    public Texture getTexture() {
        return texture;
    }
    public void setTexture(Texture texture) {
        this.texture = texture;
    }
}