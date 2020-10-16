package org.liquiduser.stur.voxel.tiles;

public class Tile {
    byte id;
    public static TileAir air = new TileAir();
    boolean isActive;
    public Tile(byte id){
        this.id = id;
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
}
