package org.liquiduser.stur.voxel.tiles;

import org.joml.Vector4f;

public class TileGrass extends Tile{
    public TileGrass() {
        super((byte)1);
        texCoords.x = 1;
    }
    @Override
    public Vector4f getColor() {
        return new Vector4f((float) Math.random(),(float) Math.random(),(float) Math.random(),1);
    }
}
