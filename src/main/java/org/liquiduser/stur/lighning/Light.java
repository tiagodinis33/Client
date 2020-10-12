package org.liquiduser.stur.lighning;

import org.joml.Vector3f;

public class Light {
    private Vector3f color;
    private Vector3f pos;
    private float attenuation;

    public Vector3f getColor() {
        return color;
    }

    public float getAttenuation() {
        return attenuation;
    }

    public Vector3f getPos() {
        return pos;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public void setAttenuation(float attenuation) {
        this.attenuation = attenuation;
    }

    public void setPos(Vector3f pos) {
        this.pos = pos;
    }
    public Light(Vector3f pos, Vector3f color, float attenuation){
        this.attenuation = attenuation;
        this.color = color;
        this.pos = pos;
    }
}
