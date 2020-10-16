package org.liquiduser.stur.voxel;

import org.joml.Vector3f;
import org.liquiduser.stur.engine.Model;

public class Chunk

{
    boolean isActive;
    byte[][][] tiles;
    /** Model correspondente a este chunk*/
    Model chunkModel;
    Vector3f pos;
    public Chunk(float x, float y, float z){
        this(new Vector3f(x,y,z));
    }
    public Chunk(Vector3f v){
        pos = v;
    }

    public Vector3f getPos() {
        return pos;
    }

    public void setPos(Vector3f pos) {
        this.pos = pos;
    }
    public void update(){


    }
    public void render(){

    }
    public void rebuild(){

    }
    public void dispose(){

    }
    private void checkTileInView(){

    }
}
