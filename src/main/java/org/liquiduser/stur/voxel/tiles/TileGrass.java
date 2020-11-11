package org.liquiduser.stur.voxel.tiles;

import org.joml.Vector4f;

public class TileGrass extends Tile{
    public TileGrass() {
        super((byte) 1);
        texCoordstop.x = 1;
        texCoordsback.x = 2;
        texCoordsfront.x = 2;
        texCoordsright.x = 2;
        texCoordsleft.x = 2;
        texCoordsbottom.x = 3;
        isActive = true;
    }
}
