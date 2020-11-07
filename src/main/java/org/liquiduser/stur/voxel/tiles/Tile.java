package org.liquiduser.stur.voxel.tiles;

import org.joml.*;

import java.util.ArrayList;

public class Tile {
    byte id;
    public static TileAir air;
    public static TileGrass grass;
    public static ArrayList<Tile> tiles;
    static {
        tiles = new ArrayList<>();

        air =  new TileAir();
        grass =  new TileGrass();

    }
    boolean isActive;
    public Tile(byte id){
        this.id = id;
        tiles.add(this);
    }
    public static Tile getTileById(byte id){
        for(var tile: tiles){
            if(tile.id == id){
                return tile;
            }
        }
        return air;
    }
    Vector4f color = new Vector4f(0,0,0,1);
    Vector2i texCoords = new Vector2i();

    public Vector2i getTexCoords() {
        return texCoords;
    }

    public void setTexCoords(Vector2i texCoords) {
        this.texCoords = texCoords;
    }

    public Vector4f getColor() {
        return color;
    }

    public void setColor(Vector4f color) {
        this.color = color;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public byte getId() {
        return id;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public AABBf getBoundingBox() {
        return new AABBf(0,0,0,1,1,1);
    }


}
