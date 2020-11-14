package org.liquiduser.stur.voxel.tiles;

import org.joml.*;

import java.util.ArrayList;

public class Tile {
    byte id;
    public static ArrayList<Tile> tiles = new ArrayList<>();
    public static TileAir air = new TileAir();
    public static TileGrass grass = new TileGrass();
    public static TileStone stone = new TileStone();
    public static TileDirt dirt = new TileDirt();
    boolean isActive = true;
    public Tile(byte id){
        this.id = id;
        tiles.add(this);
    }
    public void setTexCoordsAllSides(int x, int y){
        texCoordsback.x = x;
        texCoordsback.y = y;
        texCoordsbottom.x = x;
        texCoordsbottom.y = y;
        texCoordsright.x = x;
        texCoordsright.y = y;
        texCoordsleft.x = x;
        texCoordsleft.y = y;
        texCoordstop.x = x;
        texCoordstop.y = y;
        texCoordsfront.x = x;
        texCoordsfront.y = y;
    }
    public static Tile getTileById(byte id){
        for(var tile: tiles){
            if(tile.id == id){
                return tile;
            }
        }
        return air;
    }
    Vector2i texCoordsfront = new Vector2i();
    Vector2i texCoordsback = new Vector2i();
    Vector2i texCoordstop = new Vector2i();
    Vector2i texCoordsbottom = new Vector2i();
    Vector2i texCoordsright = new Vector2i();
    Vector2i texCoordsleft = new Vector2i();

    public Vector2i getTexCoordsback() {
        return texCoordsback;
    }

    public void setTexCoordsback(Vector2i texCoordsback) {
        this.texCoordsback = texCoordsback;
    }

    public void setTexCoordsbottom(Vector2i texCoordsbottom) {
        this.texCoordsbottom = texCoordsbottom;
    }

    public void setTexCoordsfront(Vector2i texCoordsfront) {
        this.texCoordsfront = texCoordsfront;
    }

    public void setTexCoordsleft(Vector2i texCoordsleft) {
        this.texCoordsleft = texCoordsleft;
    }

    public void setTexCoordsright(Vector2i texCoordsright) {
        this.texCoordsright = texCoordsright;
    }

    public void setTexCoordstop(Vector2i texCoordstop) {
        this.texCoordstop = texCoordstop;
    }

    public Vector2i getTexCoordsbottom() {
        return texCoordsbottom;
    }

    public Vector2i getTexCoordsfront() {
        return texCoordsfront;
    }

    public Vector2i getTexCoordsright() {
        return texCoordsright;
    }

    public Vector2i getTexCoordsleft() {
        return texCoordsleft;
    }

    public Vector2i getTexCoordstop() {
        return texCoordstop;
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
